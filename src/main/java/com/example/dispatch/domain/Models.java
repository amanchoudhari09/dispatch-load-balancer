package com.example.dispatch.domain;

import java.util.List;

public final class Models {
    private Models() { }

    public enum Priority { HIGH, MEDIUM, LOW }

    public record Order(String orderId, double latitude, double longitude, String address,
                        double packageWeight, Priority priority) { }

    public record Vehicle(String vehicleId, double capacity, double currentLatitude,
                          double currentLongitude, String currentAddress) { }

    public record DispatchPlan(String vehicleId, double capacity, double currentLatitude,
                               double currentLongitude, String currentAddress, double totalLoad,
                               double totalDistance, List<Order> assignedOrders) {
        public DispatchPlan {
            assignedOrders = List.copyOf(assignedOrders);
        }
    }

    public record DispatchResult(List<DispatchPlan> plans, List<Order> unassignedOrders) {
        public DispatchResult {
            plans = List.copyOf(plans);
            unassignedOrders = List.copyOf(unassignedOrders);
        }
    }
}
