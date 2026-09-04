package com.example.dispatch.controller;

import com.example.dispatch.domain.Models.DispatchPlan;
import com.example.dispatch.domain.Models.DispatchResult;
import com.example.dispatch.domain.Models.Order;
import com.example.dispatch.dto.DispatchDtos.AcceptedResponse;
import com.example.dispatch.dto.DispatchDtos.OrderRequest;
import com.example.dispatch.dto.DispatchDtos.OrdersRequest;
import com.example.dispatch.dto.DispatchDtos.PlanOrder;
import com.example.dispatch.dto.DispatchDtos.PlanResponse;
import com.example.dispatch.dto.DispatchDtos.PlanVehicle;
import com.example.dispatch.dto.DispatchDtos.VehicleRequest;
import com.example.dispatch.dto.DispatchDtos.VehiclesRequest;
import com.example.dispatch.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatch")
@Tag(name = "Dispatch", description = "Capacity-aware delivery dispatch planning")
public class DispatchController {
    private final DispatchService service;

    public DispatchController(DispatchService service) {
        this.service = service;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Replace the current delivery orders")
    @ApiResponses({@ApiResponse(responseCode = "202", description = "Orders accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Duplicate order IDs")})
    public AcceptedResponse orders(@Valid @RequestBody OrdersRequest request) {
        service.acceptOrders(request);
        return new AcceptedResponse("success", "Delivery orders accepted.");
    }

    @PostMapping("/vehicles")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Replace the current fleet vehicles")
    @ApiResponses({@ApiResponse(responseCode = "202", description = "Vehicles accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Duplicate vehicle IDs")})
    public AcceptedResponse vehicles(@Valid @RequestBody VehiclesRequest request) {
        service.acceptVehicles(request);
        return new AcceptedResponse("success", "Vehicle details accepted.");
    }

    @GetMapping("/plan")
    @Operation(summary = "Generate a deterministic dispatch plan")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Dispatch plan generated"),
            @ApiResponse(responseCode = "404", description = "Orders or vehicles have not been submitted")})
    public PlanResponse plan() {
        DispatchResult result = service.plan();
        return new PlanResponse(result.plans().stream().map(this::toPlanVehicle).toList(),
                result.unassignedOrders().stream().map(this::toPlanOrder).toList());
    }

    private PlanVehicle toPlanVehicle(DispatchPlan plan) {
        return new PlanVehicle(plan.vehicleId(), plan.capacity(), plan.currentLatitude(), plan.currentLongitude(),
                plan.currentAddress(), plan.totalLoad(), plan.totalDistance(),
                plan.assignedOrders().stream().map(this::toPlanOrder).toList());
    }

    private PlanOrder toPlanOrder(Order order) {
        return new PlanOrder(order.orderId(), order.latitude(), order.longitude(), order.address(),
                order.packageWeight(), order.priority());
    }
}
