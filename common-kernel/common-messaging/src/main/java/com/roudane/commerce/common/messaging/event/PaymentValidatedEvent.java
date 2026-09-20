package com.roudane.commerce.common.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentValidatedEvent(
        UUID orderId,
        UUID paymentId,
        BigDecimal amount,
        Instant occurredAt
) {
}