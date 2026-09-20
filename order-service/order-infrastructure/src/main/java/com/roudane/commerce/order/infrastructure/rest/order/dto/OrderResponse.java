package com.roudane.commerce.order.infrastructure.rest.order.dto;

import com.roudane.commerce.order.domain.model.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        List<OrderLineResponse> lines,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {
    public record OrderLineResponse(String productId, int quantity, BigDecimal unitPrice) {
    }

    public static OrderResponse from(Order order) {
        List<OrderLineResponse> lines = order.getLines().stream()
                .map(l -> new OrderLineResponse(l.getProductId(), l.getQuantity(), l.getUnitPrice()))
                .toList();

        return new OrderResponse(
                order.getId().value(),
                order.getUserId().value(),
                lines,
                order.totalAmount(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}
