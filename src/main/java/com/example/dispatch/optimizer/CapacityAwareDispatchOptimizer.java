package com.example.dispatch.optimizer;

import com.example.dispatch.domain.Models.*;
import com.example.dispatch.distance.DistanceCalculator;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CapacityAwareDispatchOptimizer {
    private final DistanceCalculator distance;
    public CapacityAwareDispatchOptimizer(DistanceCalculator distance){this.distance=distance;}
    public DispatchResult optimize(List<Order> input,List<Vehicle> vehicles){
        List<Order> orders=new ArrayList<>(input); orders.sort(Comparator.comparing(Order::priority).thenComparing(Order::orderId));
        Map<String,State> states=new LinkedHashMap<>(); vehicles.stream().sorted(Comparator.comparing(Vehicle::vehicleId)).forEach(v->states.put(v.vehicleId(),new State(v,distance)));
        List<Order> unassigned=new ArrayList<>();
        for(Order o:orders){State best=null; double cost=Double.POSITIVE_INFINITY; for(State s:states.values()) if(s.remaining()>=o.packageWeight()){double c=s.incremental(o,distance); if(c<cost-1e-9){cost=c;best=s;}} if(best==null) unassigned.add(o); else best.add(o);}
        return new DispatchResult(states.values().stream().map(State::plan).toList(),unassigned);
    }
    private static final class State {final Vehicle v; final DistanceCalculator distance; final List<Order> route=new ArrayList<>(); double load,dist; State(Vehicle v,DistanceCalculator distance){this.v=v;this.distance=distance;} double remaining(){return v.capacity()-load;} double incremental(Order o,DistanceCalculator d){if(route.isEmpty())return d.calculateDistance(v.currentLatitude(),v.currentLongitude(),o.latitude(),o.longitude()); Order last=route.get(route.size()-1); return d.calculateDistance(last.latitude(),last.longitude(),o.latitude(),o.longitude());} void add(Order o){dist+=incremental(o,distance);load+=o.packageWeight();route.add(o);} DispatchPlan plan(){return new DispatchPlan(v.vehicleId(),v.capacity(),load,dist,route);}}
}
