package com.roudane.commerce.order.application.port.in.order;

import com.roudane.commerce.order.application.command.CreateOrderCommand;
import com.roudane.commerce.order.domain.model.Order;

public interface CreateOrderUseCase {
    Order handle(CreateOrderCommand command);
}