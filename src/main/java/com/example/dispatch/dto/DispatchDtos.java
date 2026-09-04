package com.example.dispatch.dto;

import com.example.dispatch.domain.Models.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public final class DispatchDtos {
    private DispatchDtos() {}
    public record OrderRequest(@NotBlank String orderId,@DecimalMin("-90") @DecimalMax("90") double latitude,@DecimalMin("-180") @DecimalMax("180") double longitude,@NotBlank String address,@Positive double packageWeight,@NotNull Priority priority) {}
    public record OrdersRequest(@NotEmpty List<@Valid OrderRequest> orders) {}
    public record VehicleRequest(@NotBlank String vehicleId,@Positive double capacity,@DecimalMin("-90") @DecimalMax("90") double currentLatitude,@DecimalMin("-180") @DecimalMax("180") double currentLongitude,@NotBlank String currentAddress) {}
    public record VehiclesRequest(@NotEmpty List<@Valid VehicleRequest> vehicles) {}
    public record AcceptedResponse(String status,String message) {}
    public record PlanOrder(String orderId,double latitude,double longitude,String address,double packageWeight,Priority priority) {}
    public record PlanVehicle(String vehicleId,double capacity,double totalLoad,double totalDistance,List<PlanOrder> assignedOrders) {}
    public record PlanResponse(List<PlanVehicle> dispatchPlan,List<PlanOrder> unassignedOrders) {}
}
