package com.roudane.commerce.payment.application.usecase;


import com.roudane.commerce.common.annotation.LogBusinessAction;
import com.roudane.commerce.common.messaging.event.EventTopic;
import com.roudane.commerce.common.messaging.event.PaymentFailedEvent;
import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import com.roudane.commerce.payment.application.port.in.FailPaymentUseCase;
import com.roudane.commerce.payment.domain.exception.PaymentNotFoundException;
import com.roudane.commerce.payment.domain.model.OrderId;
import com.roudane.commerce.payment.domain.model.Payment;
import com.roudane.commerce.payment.domain.model.PaymentId;
import com.roudane.commerce.payment.domain.model.PaymentStatus;
import com.roudane.commerce.payment.domain.port.out.PaymentPersistencePort;
import com.roudane.commerce.payment.domain.port.out.PaymentRepositoryPort;

import java.time.Instant;
import java.util.UUID;

public class FailPaymentUseCaseImpl implements FailPaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentPersistencePort paymentPersistencePort;
    private final EventSerializerPort eventSerializerPort;

    public FailPaymentUseCaseImpl(PaymentRepositoryPort paymentRepositoryPort,
                                  PaymentPersistencePort paymentPersistencePort,
                                  EventSerializerPort eventSerializerPort) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.paymentPersistencePort = paymentPersistencePort;
        this.eventSerializerPort = eventSerializerPort;
    }

    @Override
    @LogBusinessAction("Échec du règlement de paiement")
    public void handle(UUID orderIdRaw) {
        OrderId orderId = new OrderId(orderIdRaw);

        Payment payment = paymentRepositoryPort.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(new PaymentId(orderIdRaw)));

        if (payment.getStatus() == PaymentStatus.FAILED || payment.getStatus() == PaymentStatus.SETTLED) {
            return; // idempotence
        }

        payment.fail();

        var event = new PaymentFailedEvent(
                payment.getOrderId().value(),
                payment.getId().value(),
                payment.getAmount(),
                Instant.now(),
                "Règlement refusé"
        );
        String eventPayload = eventSerializerPort.serialize(event);

        paymentPersistencePort.saveAndEnqueueEvent(payment, EventTopic.PAYMENT_FAILED.getName(), eventPayload);
    }
}
