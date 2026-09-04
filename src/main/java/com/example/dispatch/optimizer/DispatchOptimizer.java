package com.example.dispatch.optimizer;

import com.example.dispatch.domain.Models.DispatchResult;
import com.example.dispatch.domain.Models.Order;
import com.example.dispatch.domain.Models.Vehicle;

import java.util.List;

public interface DispatchOptimizer {
    DispatchResult optimize(List<Order> orders, List<Vehicle> vehicles);
}
