package com.roudane.commerce.order.domain.exception;

import com.roudane.commerce.common.exceptions.ValidationException;

public class InvalidOrderLineException extends ValidationException {

    public InvalidOrderLineException(String message) {
        super("ORDER_LINE_INVALID", message);
    }
}
