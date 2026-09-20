package com.roudane.commerce.order.application.port.in.order;

import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.UserId;

import java.util.List;

public interface GetOrdersByUserUseCase {
    List<Order> handle(UserId userId);
}
