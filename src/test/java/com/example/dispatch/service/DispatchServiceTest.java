package com.example.dispatch.service;

import com.example.dispatch.dto.DispatchDtos.OrderRequest;
import com.example.dispatch.dto.DispatchDtos.OrdersRequest;
import com.example.dispatch.dto.DispatchDtos.VehicleRequest;
import com.example.dispatch.dto.DispatchDtos.VehiclesRequest;
import com.example.dispatch.domain.Models.Priority;
import com.example.dispatch.exception.DispatchExceptions.DuplicateResourceException;
import com.example.dispatch.exception.DispatchExceptions.InvalidDispatchStateException;
import com.example.dispatch.optimizer.DispatchOptimizer;
import com.example.dispatch.repository.InMemoryRepositories;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DispatchServiceTest {
    private final InMemoryRepositories repository = new InMemoryRepositories();
    private final DispatchOptimizer optimizer = mock(DispatchOptimizer.class);
    private final DispatchService service = new DispatchService(repository, optimizer);

    @Test
    void rejectsDuplicateOrderIds() {
        OrderRequest first = new OrderRequest("O1", 0, 0, "A", 1, Priority.HIGH);
        OrderRequest second = new OrderRequest("O1", 1, 1, "B", 1, Priority.LOW);

        assertThatThrownBy(() -> service.acceptOrders(new OrdersRequest(List.of(first, second))))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void delegatesAcceptedOrdersToRepository() {
        OrderRequest order = new OrderRequest("O1", 0, 0, "A", 1, Priority.HIGH);
        service.acceptOrders(new OrdersRequest(List.of(order)));
        assertThatThrownBy(service::plan).isInstanceOf(InvalidDispatchStateException.class);
    }

    @Test
    void rejectsPlanWithoutOrdersOrVehicles() {
        assertThatThrownBy(service::plan).isInstanceOf(InvalidDispatchStateException.class);
        VehicleRequest vehicle = new VehicleRequest("V1", 10, 0, 0, "Depot");
        service.acceptVehicles(new VehiclesRequest(List.of(vehicle)));
        assertThatThrownBy(service::plan).isInstanceOf(InvalidDispatchStateException.class);
    }
}
