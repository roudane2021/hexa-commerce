package com.roudane.commerce.payment.infrastructure.rest.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AuthorizePaymentRequest(
        @NotNull UUID orderId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount
) {
}
