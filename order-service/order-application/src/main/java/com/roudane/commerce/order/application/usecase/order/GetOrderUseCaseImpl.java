package com.roudane.commerce.order.application.usecase.order;

import com.roudane.commerce.common.annotation.LogBusinessAction;
import com.roudane.commerce.order.application.port.in.order.GetOrderUseCase;
import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.OrderId;
import com.roudane.commerce.order.domain.port.out.OrderRepositoryPort;

import java.util.Optional;

public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrderUseCaseImpl(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    @LogBusinessAction("Récuperer  commande par ID ")
    public Optional<Order> handle(OrderId id) {
        return orderRepositoryPort.findById(id);
    }
}