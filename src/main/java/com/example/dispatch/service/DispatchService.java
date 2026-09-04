package com.example.dispatch.service;

import com.example.dispatch.domain.Models.*;
import com.example.dispatch.dto.DispatchDtos.*;
import com.example.dispatch.optimizer.CapacityAwareDispatchOptimizer;
import com.example.dispatch.repository.InMemoryRepositories;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DispatchService {
 private final InMemoryRepositories repo; private final CapacityAwareDispatchOptimizer optimizer;
 public DispatchService(InMemoryRepositories repo,CapacityAwareDispatchOptimizer optimizer){this.repo=repo;this.optimizer=optimizer;}
 public void acceptOrders(OrdersRequest request){ensureUnique(request.orders().stream().map(OrderRequest::orderId).toList(),"order"); repo.replaceOrders(request.orders().stream().map(this::toOrder).toList());}
 public void acceptVehicles(VehiclesRequest request){ensureUnique(request.vehicles().stream().map(VehicleRequest::vehicleId).toList(),"vehicle"); repo.replaceVehicles(request.vehicles().stream().map(this::toVehicle).toList());}
 public DispatchResult plan(){if(repo.orders().isEmpty()) throw new IllegalStateException("No orders are available"); if(repo.vehicles().isEmpty()) throw new IllegalStateException("No vehicles are available"); return optimizer.optimize(repo.orders(),repo.vehicles());}
 private Order toOrder(OrderRequest r){return new Order(r.orderId(),r.latitude(),r.longitude(),r.address(),r.packageWeight(),r.priority());}
 private Vehicle toVehicle(VehicleRequest r){return new Vehicle(r.vehicleId(),r.capacity(),r.currentLatitude(),r.currentLongitude(),r.currentAddress());}
 private void ensureUnique(List<String> ids,String type){if(ids.stream().distinct().count()!=ids.size()) throw new IllegalArgumentException("Duplicate "+type+" IDs are not allowed");}
}
