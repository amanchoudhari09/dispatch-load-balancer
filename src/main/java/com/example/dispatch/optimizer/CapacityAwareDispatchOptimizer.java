package com.example.dispatch.optimizer;

import com.example.dispatch.domain.Models.DispatchPlan;
import com.example.dispatch.domain.Models.DispatchResult;
import com.example.dispatch.domain.Models.Order;
import com.example.dispatch.domain.Models.Vehicle;
import com.example.dispatch.distance.DistanceCalculator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CapacityAwareDispatchOptimizer implements DispatchOptimizer {
    private static final double EPSILON = 1.0e-9;
    private final DistanceCalculator distanceCalculator;

    public CapacityAwareDispatchOptimizer(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public DispatchResult optimize(List<Order> inputOrders, List<Vehicle> inputVehicles) {
        List<Order> orders = new ArrayList<>(inputOrders);
        orders.sort(Comparator.comparing(Order::priority).thenComparing(Order::orderId));
        Map<String, VehicleState> states = new LinkedHashMap<>();
        inputVehicles.stream().sorted(Comparator.comparing(Vehicle::vehicleId))
                .forEach(vehicle -> states.put(vehicle.vehicleId(), new VehicleState(vehicle)));

        List<Order> unassigned = new ArrayList<>();
        for (Order order : orders) {
            VehicleState best = null;
            double bestDistance = Double.POSITIVE_INFINITY;
            for (VehicleState candidate : states.values()) {
                if (candidate.remainingCapacity() + EPSILON < order.packageWeight()) continue;
                double candidateDistance = candidate.incrementalDistance(order);
                if (best == null || isPreferred(candidate, candidateDistance, best, bestDistance)) {
                    best = candidate;
                    bestDistance = candidateDistance;
                }
            }
            if (best == null) unassigned.add(order);
            else best.assign(order);
        }

        states.values().forEach(VehicleState::improveRoute);
        return new DispatchResult(states.values().stream().map(VehicleState::toPlan).toList(), unassigned);
    }

    private boolean isPreferred(VehicleState candidate, double candidateDistance,
                                VehicleState current, double currentDistance) {
        if (candidateDistance < currentDistance - EPSILON) return true;
        if (Math.abs(candidateDistance - currentDistance) > EPSILON) return false;
        if (candidate.remainingCapacity() > current.remainingCapacity() + EPSILON) return true;
        return Math.abs(candidate.remainingCapacity() - current.remainingCapacity()) <= EPSILON
                && candidate.vehicle.vehicleId().compareTo(current.vehicle.vehicleId()) < 0;
    }

    private final class VehicleState {
        private final Vehicle vehicle;
        private final List<Order> route = new ArrayList<>();
        private double load;

        private VehicleState(Vehicle vehicle) { this.vehicle = vehicle; }
        private double remainingCapacity() { return vehicle.capacity() - load; }

        private double incrementalDistance(Order order) {
            if (route.isEmpty()) {
                return distanceCalculator.calculateDistance(vehicle.currentLatitude(), vehicle.currentLongitude(),
                        order.latitude(), order.longitude());
            }
            Order last = route.get(route.size() - 1);
            return distanceCalculator.calculateDistance(last.latitude(), last.longitude(),
                    order.latitude(), order.longitude());
        }

        private void assign(Order order) {
            route.add(order);
            load += order.packageWeight();
        }

        // 2-opt is a deterministic local-search heuristic. It changes route order only,
        // so assignments and capacity remain unchanged; it does not guarantee global optimality.
        private void improveRoute() {
            boolean improved = true;
            while (improved) {
                improved = false;
                double currentDistance = totalDistance();
                for (int start = 0; start < route.size() - 1 && !improved; start++) {
                    for (int end = start + 1; end < route.size(); end++) {
                        List<Order> candidate = new ArrayList<>(route);
                        java.util.Collections.reverse(candidate.subList(start, end + 1));
                        double candidateDistance = totalDistance(candidate);
                        if (candidateDistance < currentDistance - EPSILON) {
                            route.clear();
                            route.addAll(candidate);
                            improved = true;
                            break;
                        }
                    }
                }
            }
        }

        private DispatchPlan toPlan() {
            return new DispatchPlan(vehicle.vehicleId(), vehicle.capacity(), vehicle.currentLatitude(),
                    vehicle.currentLongitude(), vehicle.currentAddress(), load, totalDistance(), route);
        }

        private double totalDistance() { return totalDistance(route); }

        private double totalDistance(List<Order> routeToMeasure) {
            double total = 0.0;
            double previousLatitude = vehicle.currentLatitude();
            double previousLongitude = vehicle.currentLongitude();
            for (Order order : routeToMeasure) {
                total += distanceCalculator.calculateDistance(previousLatitude, previousLongitude,
                        order.latitude(), order.longitude());
                previousLatitude = order.latitude();
                previousLongitude = order.longitude();
            }
            return total;
        }
    }
}
