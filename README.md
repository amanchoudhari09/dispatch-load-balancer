# Dispatch Load Balancer

Backend-only Spring Boot 3 REST API for capacity-aware delivery dispatching.

## Run

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## API

1. `POST /api/dispatch/orders` with `{ "orders": [...] }`
2. `POST /api/dispatch/vehicles` with `{ "vehicles": [...] }`
3. `GET /api/dispatch/plan`

The API validates coordinates, positive weights/capacities, non-empty batches, priorities, and duplicate IDs. Orders that exceed available capacity are returned in `unassignedOrders`.

## Algorithm

`CapacityAwareDispatchOptimizer` processes orders in deterministic HIGH, MEDIUM, LOW priority order. For each order it selects the feasible vehicle with the lowest incremental route distance, using Haversine distance in kilometers; ties are resolved by vehicle ID. This is a deterministic greedy heuristic, not a globally optimal vehicle-routing solver. The optimizer is isolated behind a replaceable component seam.

For a route, distance is the vehicle-to-first-stop distance plus each consecutive-stop distance. Haversine uses Earth radius 6371.0088 km. The greedy assignment is approximately O(O × V), plus sorting, and uses O(O + V) route state.

## Design

Controllers handle HTTP and validation only. Services coordinate repositories and optimization. The repository is an in-memory replacement store to keep the assignment focused on optimization; no external maps API or authentication is required.

## Limitations and future work

The current route insertion appends stops and does not solve time windows, vehicle return-to-depot routing, traffic, or globally optimal route sequencing. A future strategy can implement exact or metaheuristic VRP optimization, persistent storage, and asynchronous planning for very large fleets.
