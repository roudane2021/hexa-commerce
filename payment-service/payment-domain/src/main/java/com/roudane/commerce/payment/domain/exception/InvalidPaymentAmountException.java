package com.roudane.commerce.payment.domain.exception;

import com.roudane.commerce.common.exceptions.ValidationException;

public class InvalidPaymentAmountException extends ValidationException {

    public InvalidPaymentAmountException(final  String message) {
        super("PAYMENT_INVALID_AMOUNT", message);
    }
}
