package com.roudane.commerce.order.domain.exception;

import com.roudane.commerce.common.exceptions.ValidationException;

public class EmptyOrderException extends ValidationException {

    public EmptyOrderException() {
        super("ORDER_EMPTY", "Une commande doit contenir au moins une ligne");
    }
}
