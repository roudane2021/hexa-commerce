package com.roudane.commerce.order.application.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID userId,
        List<OrderLineCommand> lines
) {
    public record OrderLineCommand(String productId, int quantity, BigDecimal unitPrice) {
    }
}
