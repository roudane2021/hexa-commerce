package com.roudane.commerce.order.application.port.in.order;

import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.OrderId;

import java.util.Optional;

public interface GetOrderUseCase {
    Optional<Order> handle(OrderId id);
}
