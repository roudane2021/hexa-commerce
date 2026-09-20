package com.roudane.commerce.payment.infrastructure.persistence.payment.adapter;



import com.roudane.commerce.payment.domain.model.OrderId;
import com.roudane.commerce.payment.domain.model.Payment;
import com.roudane.commerce.payment.domain.model.PaymentId;
import com.roudane.commerce.payment.domain.port.out.PaymentRepositoryPort;
import com.roudane.commerce.payment.infrastructure.persistence.payment.entities.PaymentJpaEntity;
import com.roudane.commerce.payment.infrastructure.persistence.payment.mapper.PaymentMapper;
import com.roudane.commerce.payment.infrastructure.persistence.payment.repository.PaymentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaPaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository jpaRepository;

    public JpaPaymentRepositoryAdapter(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity saved = jpaRepository.save(PaymentMapper.toEntity(payment));
        return PaymentMapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return jpaRepository.findById(id.value()).map(PaymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(OrderId orderId) {
        return jpaRepository.findByOrderId(orderId.value()).map(PaymentMapper::toDomain);
    }
}
