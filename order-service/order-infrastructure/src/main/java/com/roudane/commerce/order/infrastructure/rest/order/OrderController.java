package com.roudane.commerce.order.infrastructure.rest.order;

import com.roudane.commerce.order.application.command.CreateOrderCommand;

import com.roudane.commerce.order.application.port.in.order.CreateOrderUseCase;
import com.roudane.commerce.order.application.port.in.order.GetOrdersByUserUseCase;
import com.roudane.commerce.order.domain.model.UserId;
import com.roudane.commerce.order.infrastructure.rest.order.dto.CreateOrderRequest;
import com.roudane.commerce.order.infrastructure.rest.order.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        var lines = request.lines().stream()
                .map(l -> new CreateOrderCommand.OrderLineCommand(l.productId(), l.quantity(), l.unitPrice()))
                .toList();

        var command = new CreateOrderCommand(request.userId(), lines);
        var order = createOrderUseCase.handle(command);

        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getByUser(@PathVariable("userId") UUID userId) {
        var orders = getOrdersByUserUseCase.handle(new UserId(userId));
        return ResponseEntity.ok(orders.stream().map(OrderResponse::from).toList());
    }
}