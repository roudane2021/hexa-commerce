package com.roudane.commerce.payment.infrastructure.rest.payment.dto;

import com.roudane.commerce.payment.domain.model.Payment;

import java.util.UUID;

public record AuthorizePaymentResponse(UUID paymentId, String status) {

    public static AuthorizePaymentResponse from(Payment payment) {
        return new AuthorizePaymentResponse(payment.getId().value(), payment.getStatus().name());
    }
}