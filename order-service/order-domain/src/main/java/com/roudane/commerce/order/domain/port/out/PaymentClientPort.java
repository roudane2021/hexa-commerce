package com.roudane.commerce.order.domain.port.out;

import com.roudane.commerce.order.domain.model.OrderId;

import java.math.BigDecimal;

public interface PaymentClientPort {
    PaymentAuthorizationResult authorize(OrderId orderId, BigDecimal amount);

    record PaymentAuthorizationResult(boolean accepted, String paymentId) {
    }
}