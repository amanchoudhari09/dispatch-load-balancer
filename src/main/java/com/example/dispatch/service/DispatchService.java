package com.example.dispatch.service;

import com.example.dispatch.domain.Models.DispatchResult;
import com.example.dispatch.domain.Models.Order;
import com.example.dispatch.domain.Models.Vehicle;
import com.example.dispatch.dto.DispatchDtos.OrderRequest;
import com.example.dispatch.dto.DispatchDtos.OrdersRequest;
import com.example.dispatch.dto.DispatchDtos.VehicleRequest;
import com.example.dispatch.dto.DispatchDtos.VehiclesRequest;
import com.example.dispatch.exception.DispatchExceptions.DuplicateResourceException;
import com.example.dispatch.exception.DispatchExceptions.InvalidDispatchStateException;
import com.example.dispatch.optimizer.DispatchOptimizer;
import com.example.dispatch.repository.InMemoryRepositories;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DispatchService {
    private final InMemoryRepositories repository;
    private final DispatchOptimizer optimizer;

    public DispatchService(InMemoryRepositories repository, DispatchOptimizer optimizer) {
        this.repository = repository;
        this.optimizer = optimizer;
    }

    public void acceptOrders(OrdersRequest request) {
        ensureUnique(request.orders().stream().map(OrderRequest::orderId).toList(), "order");
        repository.replaceOrders(request.orders().stream().map(this::toOrder).toList());
    }

    public void acceptVehicles(VehiclesRequest request) {
        ensureUnique(request.vehicles().stream().map(VehicleRequest::vehicleId).toList(), "vehicle");
        repository.replaceVehicles(request.vehicles().stream().map(this::toVehicle).toList());
    }

    public DispatchResult plan() {
        List<Order> orders = repository.orders();
        List<Vehicle> vehicles = repository.vehicles();
        if (orders.isEmpty()) {
            throw new InvalidDispatchStateException("No orders are available; submit orders before requesting a plan");
        }
        if (vehicles.isEmpty()) {
            throw new InvalidDispatchStateException("No vehicles are available; submit vehicles before requesting a plan");
        }
        return optimizer.optimize(orders, vehicles);
    }

    private Order toOrder(OrderRequest request) {
        return new Order(request.orderId(), request.latitude(), request.longitude(), request.address(),
                request.packageWeight(), request.priority());
    }

    private Vehicle toVehicle(VehicleRequest request) {
        return new Vehicle(request.vehicleId(), request.capacity(), request.currentLatitude(),
                request.currentLongitude(), request.currentAddress());
    }

    private void ensureUnique(List<String> ids, String resourceType) {
        Set<String> uniqueIds = new HashSet<>(ids);
        if (uniqueIds.size() != ids.size()) {
            throw new DuplicateResourceException("Duplicate " + resourceType + " IDs are not allowed");
        }
    }
}
