package com.roudane.commerce.payment.infrastructure.persistence.payment.repository;


import com.roudane.commerce.payment.infrastructure.persistence.payment.entities.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, UUID> {
    Optional<PaymentJpaEntity> findByOrderId(UUID orderId);
}