package com.roudane.commerce.order.application.usecase.order;


import com.roudane.commerce.common.annotation.LogBusinessAction;
import com.roudane.commerce.order.application.port.in.order.ConfirmOrderUseCase;
import com.roudane.commerce.order.domain.exception.OrderNotFoundException;
import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.OrderId;
import com.roudane.commerce.order.domain.model.OrderStatus;
import com.roudane.commerce.order.domain.port.out.OrderRepositoryPort;

import java.util.UUID;

public class ConfirmOrderUseCaseImpl implements ConfirmOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public ConfirmOrderUseCaseImpl(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    @LogBusinessAction("Confirme commande")
    public void handle(UUID orderIdRaw) {
        OrderId orderId = new OrderId(orderIdRaw);

        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Idempotence : le message Kafka peut être livré plusieurs fois
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            return;
        }

        order.confirm();
        orderRepositoryPort.save(order);
    }
}
