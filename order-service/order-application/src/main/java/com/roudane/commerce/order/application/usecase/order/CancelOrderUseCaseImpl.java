package com.roudane.commerce.order.application.usecase.order;


import com.roudane.commerce.common.annotation.LogBusinessAction;
import com.roudane.commerce.order.application.port.in.order.CancelOrderUseCase;
import com.roudane.commerce.order.domain.exception.OrderNotFoundException;
import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.OrderId;
import com.roudane.commerce.order.domain.model.OrderStatus;
import com.roudane.commerce.order.domain.port.out.OrderRepositoryPort;

import java.util.UUID;

public class CancelOrderUseCaseImpl implements CancelOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public CancelOrderUseCaseImpl(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    @LogBusinessAction("Annuler commande")
    public void handle(UUID orderIdRaw) {
        OrderId orderId = new OrderId(orderIdRaw);

        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return; // idempotence
        }

        order.cancel();
        orderRepositoryPort.save(order);
    }
}
