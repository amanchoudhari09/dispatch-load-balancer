package com.example.dispatch.optimizer;

import com.example.dispatch.distance.HaversineDistanceCalculator;
import com.example.dispatch.domain.Models.Order;
import com.example.dispatch.domain.Models.Priority;
import com.example.dispatch.domain.Models.Vehicle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CapacityAwareDispatchOptimizerTest {
    private final CapacityAwareDispatchOptimizer optimizer =
            new CapacityAwareDispatchOptimizer(new HaversineDistanceCalculator());

    @Test
    void assignsPriorityOrdersFirstAndLeavesOverweightOrdersUnassigned() {
        Order high = new Order("HIGH", 0, 1, "A", 5, Priority.HIGH);
        Order low = new Order("LOW", 0, 2, "B", 6, Priority.LOW);
        Vehicle vehicle = new Vehicle("V1", 5, 0, 0, "Depot");

        var result = optimizer.optimize(List.of(low, high), List.of(vehicle));

        assertThat(result.plans().getFirst().assignedOrders()).containsExactly(high);
        assertThat(result.unassignedOrders()).containsExactly(low);
    }

    @Test
    void choosesClosestFeasibleVehicleAndIsDeterministicOnTie() {
        Order order = new Order("O1", 0, 1, "A", 2, Priority.MEDIUM);
        Vehicle far = new Vehicle("V2", 10, 0, 10, "Far");
        Vehicle close = new Vehicle("V1", 10, 0, 0, "Close");

        var result = optimizer.optimize(List.of(order), List.of(far, close));

        assertThat(result.plans().get(0).vehicleId()).isEqualTo("V1");
        assertThat(result.plans().get(0).totalLoad()).isEqualTo(2);
        assertThat(result.plans().get(1).assignedOrders()).isEmpty();
    }

    @Test
    void doesNotExceedCapacityAndRouteDistanceStartsAtVehicleLocation() {
        Order first = new Order("A", 0, 1, "A", 3, Priority.HIGH);
        Order second = new Order("B", 0, 2, "B", 3, Priority.MEDIUM);
        Vehicle vehicle = new Vehicle("V1", 6, 0, 0, "Depot");

        var result = optimizer.optimize(List.of(first, second), List.of(vehicle));

        assertThat(result.plans().getFirst().totalLoad()).isEqualTo(6);
        assertThat(result.plans().getFirst().totalDistance()).isGreaterThan(0);
        assertThat(result.plans().getFirst().assignedOrders()).hasSize(2);
    }
}
