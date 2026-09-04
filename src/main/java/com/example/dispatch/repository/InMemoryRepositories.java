package com.example.dispatch.repository;

import com.example.dispatch.domain.Models.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InMemoryRepositories {
    private final Map<String,Order> orders=new LinkedHashMap<>(); private final Map<String,Vehicle> vehicles=new LinkedHashMap<>();
    public synchronized void replaceOrders(List<Order> values){orders.clear(); values.forEach(o->orders.put(o.orderId(),o));}
    public synchronized void replaceVehicles(List<Vehicle> values){vehicles.clear(); values.forEach(v->vehicles.put(v.vehicleId(),v));}
    public synchronized List<Order> orders(){return List.copyOf(orders.values());}
    public synchronized List<Vehicle> vehicles(){return List.copyOf(vehicles.values());}
}
