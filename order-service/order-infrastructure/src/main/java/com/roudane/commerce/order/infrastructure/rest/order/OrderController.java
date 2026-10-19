package com.roudane.commerce.order.infrastructure.rest.order;

import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.order.application.port.in.order.CreateOrderUseCase;
import com.roudane.commerce.order.application.port.in.order.GetOrdersByUserUseCase;
import com.roudane.commerce.order.domain.model.UserId;
import com.roudane.commerce.order.infrastructure.rest.order.dto.CreateOrderRequest;
import com.roudane.commerce.order.infrastructure.rest.order.dto.OrderResponse;
import com.roudane.commerce.order.infrastructure.rest.order.mapper.OrderRequestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrdersByUserUseCase getOrdersByUserUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrdersByUserUseCase getOrdersByUserUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrdersByUserUseCase = getOrdersByUserUseCase;
    }

    @PostMapping
    @LogTechnicalCall("Controller : Creation Order")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        // MapStruct gère la conversion complète du Request vers la Command en 1 ligne
        var command = OrderRequestMapper.INSTANCE.toCommand(request);

        var order = createOrderUseCase.handle(command);

        // Bonnes pratiques REST : retourner un statut 201 CREATED pour une création
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/user/{userId}")
    @LogTechnicalCall("Controller : Recuper  Order by userID")
    public ResponseEntity<List<OrderResponse>> getByUser(@PathVariable("userId") UUID userId) {
        var orders = getOrdersByUserUseCase.handle(new UserId(userId));
        return ResponseEntity.ok(orders.stream().map(OrderResponse::from).toList());
    }
}