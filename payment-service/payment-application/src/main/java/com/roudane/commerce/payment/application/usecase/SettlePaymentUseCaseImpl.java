package com.roudane.commerce.payment.application.usecase;



import com.roudane.commerce.common.annotation.LogBusinessAction;
import com.roudane.commerce.common.messaging.event.EventTopic;
import com.roudane.commerce.common.messaging.event.PaymentValidatedEvent;
import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import com.roudane.commerce.payment.application.port.in.SettlePaymentUseCase;
import com.roudane.commerce.payment.domain.exception.PaymentNotFoundException;
import com.roudane.commerce.payment.domain.model.OrderId;
import com.roudane.commerce.payment.domain.model.Payment;
import com.roudane.commerce.payment.domain.model.PaymentId;
import com.roudane.commerce.payment.domain.model.PaymentStatus;
import com.roudane.commerce.payment.domain.port.out.PaymentPersistencePort;
import com.roudane.commerce.payment.domain.port.out.PaymentRepositoryPort;

import java.time.Instant;
import java.util.UUID;

public class SettlePaymentUseCaseImpl implements SettlePaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;       // lecture (findByOrderId)
    private final PaymentPersistencePort paymentPersistencePort;     // écriture atomique + outbox
    private final EventSerializerPort eventSerializerPort;

    public SettlePaymentUseCaseImpl(PaymentRepositoryPort paymentRepositoryPort,
                                    PaymentPersistencePort paymentPersistencePort,
                                    EventSerializerPort eventSerializerPort) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.paymentPersistencePort = paymentPersistencePort;
        this.eventSerializerPort = eventSerializerPort;
    }

    @Override
    @LogBusinessAction("Règlement définitif du paiement")
    public void handle(UUID orderIdRaw) {
        OrderId orderId = new OrderId(orderIdRaw);

        Payment payment = paymentRepositoryPort.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(new PaymentId(orderIdRaw)));

        // Idempotence : message Kafka potentiellement livré plusieurs fois
        if (payment.getStatus() == PaymentStatus.SETTLED) {
            return;
        }

        payment.settle();

        var event = new PaymentValidatedEvent(
                payment.getOrderId().value(),
                payment.getId().value(),
                payment.getAmount(),
                Instant.now()
        );
        String eventPayload = eventSerializerPort.serialize(event);

        // Écriture ATOMIQUE : payment + événement outbox, une seule transaction
        paymentPersistencePort.saveAndEnqueueEvent(payment, EventTopic.PAYMENT_VALIDATED.getName(), eventPayload);
    }
}
