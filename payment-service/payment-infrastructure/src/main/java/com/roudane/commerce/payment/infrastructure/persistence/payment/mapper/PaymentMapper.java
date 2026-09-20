package com.roudane.commerce.payment.infrastructure.persistence.payment.mapper;


import com.roudane.commerce.payment.domain.model.OrderId;
import com.roudane.commerce.payment.domain.model.Payment;
import com.roudane.commerce.payment.domain.model.PaymentId;
import com.roudane.commerce.payment.domain.model.PaymentStatus;
import com.roudane.commerce.payment.infrastructure.persistence.payment.entities.PaymentJpaEntity;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentJpaEntity toEntity(Payment payment) {
        return new PaymentJpaEntity(
                payment.getId().value(),
                payment.getOrderId().value(),
                payment.getAmount(),
                payment.getStatus().name()
        );
    }

    public static Payment toDomain(PaymentJpaEntity entity) {
        return new Payment(
                new PaymentId(entity.getId()),
                new OrderId(entity.getOrderId()),
                entity.getAmount(),
                entity.getCreatedAt(),
                PaymentStatus.valueOf(entity.getStatus())
        );
    }
}