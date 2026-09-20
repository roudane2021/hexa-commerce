package com.roudane.commerce.order.application.usecase.order;

import com.roudane.commerce.common.annotation.LogBusinessAction;
import com.roudane.commerce.order.application.port.in.order.GetOrdersByUserUseCase;
import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.UserId;
import com.roudane.commerce.order.domain.port.out.OrderRepositoryPort;

import java.util.List;

public class GetOrdersByUserUseCaseImpl implements GetOrdersByUserUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrdersByUserUseCaseImpl(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    @LogBusinessAction("Récuperer des Commandes par User")
    public List<Order> handle(UserId userId) {
        return orderRepositoryPort.findByUserId(userId);
    }
}
