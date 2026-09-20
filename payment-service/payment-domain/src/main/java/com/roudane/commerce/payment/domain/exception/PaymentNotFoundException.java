package com.roudane.commerce.payment.domain.exception;

import com.roudane.commerce.common.exceptions.NotFoundException;
import com.roudane.commerce.payment.domain.model.PaymentId;

public class PaymentNotFoundException extends NotFoundException {

    public PaymentNotFoundException(PaymentId id) {
        super("PAYMENT_NOT_FOUND", "Paiement introuvable : " + id.value());
    }
}
