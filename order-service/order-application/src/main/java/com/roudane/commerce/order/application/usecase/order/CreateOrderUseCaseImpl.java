package com.roudane.commerce.order.application.usecase.order;

import com.roudane.commerce.common.annotation.LogBusinessAction;
import com.roudane.commerce.common.messaging.event.OrderCreatedEvent;
import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import com.roudane.commerce.order.application.command.CreateOrderCommand;
import com.roudane.commerce.order.application.port.in.order.CreateOrderUseCase;
import com.roudane.commerce.order.domain.exception.UserNotFoundException;
import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.OrderLine;
import com.roudane.commerce.order.domain.model.UserId;
import com.roudane.commerce.order.domain.port.out.OrderPersistencePort;
import com.roudane.commerce.order.domain.port.out.OrderRepositoryPort;
import com.roudane.commerce.order.domain.port.out.PaymentClientPort;
import com.roudane.commerce.order.domain.port.out.UserRepositoryPort;


import java.time.Instant;
import java.util.List;

public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PaymentClientPort paymentClientPort;
    private final EventSerializerPort eventSerializerPort;


    public CreateOrderUseCaseImpl(OrderRepositoryPort orderRepositoryPort,
                                  UserRepositoryPort userRepositoryPort,
                                  PaymentClientPort paymentClientPort,
                                  EventSerializerPort eventSerializerPort,
                                  OrderPersistencePort orderPersistencePort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.paymentClientPort = paymentClientPort;
        this.eventSerializerPort = eventSerializerPort;
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    @LogBusinessAction("Création commande")
    public Order handle(CreateOrderCommand command) {
        UserId userId = new UserId(command.userId());
        userRepositoryPort.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        List<OrderLine> lines = command.lines().stream()
                .map(l -> new OrderLine(l.productId(), l.quantity(), l.unitPrice()))
                .toList();

        Order order = Order.create(userId, lines);

        // ① Construire l'événement d'intégration EXPLICITEMENT, pas l'agrégat domaine
        var event = new OrderCreatedEvent(
                order.getId().value(),
                order.getUserId().value(),
                order.totalAmount(),
                Instant.now()
        );
        String eventPayload = eventSerializerPort.serialize(event);

        // Écriture ATOMIQUE : commande + événement outbox, une seule transaction
        Order saved = orderPersistencePort.createAndEnqueueEvent(order, eventPayload);

        // ② Appel SYNCHRONE vers Payment
        var authResult = paymentClientPort.authorize(saved.getId(), saved.totalAmount());
        if (authResult.accepted()) {
            saved.markPaymentPending();
        } else {
            saved.markPaymentRejected();
        }
        orderRepositoryPort.save(saved);

        return saved;
    }
}