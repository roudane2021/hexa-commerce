package com.roudane.commerce.payment.infrastructure.persistence.payment.adapter;

import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.common.outbox.entities.OutboxEventJpaEntity;
import com.roudane.commerce.common.outbox.repository.OutboxEventJpaRepository;
import com.roudane.commerce.payment.domain.model.Payment;
import com.roudane.commerce.payment.domain.port.out.PaymentPersistencePort;
import com.roudane.commerce.payment.infrastructure.persistence.payment.entities.PaymentJpaEntity;
import com.roudane.commerce.payment.infrastructure.persistence.payment.mapper.PaymentMapper;
import com.roudane.commerce.payment.infrastructure.persistence.payment.repository.PaymentJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class TransactionalPaymentPersistenceAdapter implements PaymentPersistencePort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final OutboxEventJpaRepository outboxEventJpaRepository;

    public TransactionalPaymentPersistenceAdapter(PaymentJpaRepository paymentJpaRepository,
                                                  OutboxEventJpaRepository outboxEventJpaRepository) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.outboxEventJpaRepository = outboxEventJpaRepository;
    }

    @Override
    @Transactional
    @LogTechnicalCall("Sauvegarde paiement + événement outbox")
    public Payment saveAndEnqueueEvent(Payment payment, String topic, String eventPayload) {
        PaymentJpaEntity savedEntity = paymentJpaRepository.save(PaymentMapper.toEntity(payment));

        OutboxEventJpaEntity outboxEvent = new OutboxEventJpaEntity(
                UUID.randomUUID(),
                topic,
                savedEntity.getOrderId().toString(),
                eventPayload
        );
        outboxEventJpaRepository.save(outboxEvent);

        return PaymentMapper.toDomain(savedEntity);
    }
}