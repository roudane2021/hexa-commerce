package com.roudane.commerce.order.domain.exception;

import com.roudane.commerce.common.exceptions.ConflictException;
import com.roudane.commerce.order.domain.model.OrderId;

public class OrderAlreadyShippedException extends ConflictException {

    public OrderAlreadyShippedException(OrderId orderId) {
        super("ORDER_ALREADY_SHIPPED", "Impossible d'annuler la commande " + orderId.value() + " : déjà expédiée");
    }
}
