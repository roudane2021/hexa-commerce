package com.roudane.commerce.payment.domain.exception;

import com.roudane.commerce.common.exceptions.ConflictException;
import com.roudane.commerce.payment.domain.model.PaymentId;

public class PaymentAlreadySettledException extends ConflictException {

    public PaymentAlreadySettledException(PaymentId id) {
        super("PAYMENT_ALREADY_SETTLED", "Le paiement " + id.value() + " est déjà réglé (SETTLED)");
    }
}