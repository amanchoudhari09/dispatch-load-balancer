package com.example.dispatch.controller;

import com.example.dispatch.domain.Models.*;
import com.example.dispatch.dto.DispatchDtos.*;
import com.example.dispatch.service.DispatchService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/dispatch")
public class DispatchController {
 private final DispatchService service; public DispatchController(DispatchService service){this.service=service;}
 @PostMapping("/orders") @Operation(summary="Accept delivery orders") public AcceptedResponse orders(@Valid @RequestBody OrdersRequest request){service.acceptOrders(request);return new AcceptedResponse("success","Delivery orders accepted.");}
 @PostMapping("/vehicles") @Operation(summary="Accept fleet details") public AcceptedResponse vehicles(@Valid @RequestBody VehiclesRequest request){service.acceptVehicles(request);return new AcceptedResponse("success","Vehicle details accepted.");}
 @GetMapping("/plan") @Operation(summary="Generate optimized dispatch plan") public PlanResponse plan(){DispatchResult r=service.plan(); return new PlanResponse(r.plans().stream().map(this::vehicle).toList(),r.unassignedOrders().stream().map(this::order).toList());}
 private PlanVehicle vehicle(DispatchPlan p){return new PlanVehicle(p.vehicleId(),p.capacity(),p.totalLoad(),p.totalDistance(),p.assignedOrders().stream().map(this::order).toList());}
 private PlanOrder order(Order o){return new PlanOrder(o.orderId(),o.latitude(),o.longitude(),o.address(),o.packageWeight(),o.priority());}
}
