package com.roudane.commerce.common.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID userId,
        BigDecimal totalAmount,
        Instant occurredAt
) {
}