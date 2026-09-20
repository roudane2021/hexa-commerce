package com.roudane.commerce.order.domain.exception;

import com.roudane.commerce.common.exceptions.NotFoundException;
import com.roudane.commerce.order.domain.model.OrderId;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(OrderId orderId) {
        super("ORDER_NOT_FOUND", "Commande introuvable : " + orderId.value());
    }
}
