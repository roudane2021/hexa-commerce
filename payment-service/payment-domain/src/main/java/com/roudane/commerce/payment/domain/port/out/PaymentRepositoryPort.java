package com.roudane.commerce.payment.domain.port.out;

import com.roudane.commerce.payment.domain.model.OrderId;
import com.roudane.commerce.payment.domain.model.Payment;
import com.roudane.commerce.payment.domain.model.PaymentId;

import java.util.Optional;

public interface PaymentRepositoryPort {

    Payment save(Payment payment);
    Optional<Payment> findById(PaymentId id);
    Optional<Payment> findByOrderId(OrderId orderId);
}
