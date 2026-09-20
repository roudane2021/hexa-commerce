package com.roudane.commerce.order.infrastructure.rest.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderLineRequest(
        @NotBlank(message = "productId est obligatoire")
        String productId,

        @Min(value = 1, message = "quantity doit être positif")
        int quantity,

        @NotNull(message = "unitPrice est obligatoire")
        BigDecimal unitPrice
) {
}
