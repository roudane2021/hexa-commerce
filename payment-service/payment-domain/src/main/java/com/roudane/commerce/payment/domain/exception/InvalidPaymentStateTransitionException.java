package com.roudane.commerce.payment.domain.exception;

import com.roudane.commerce.common.exceptions.ConflictException;
import com.roudane.commerce.payment.domain.model.PaymentId;
import com.roudane.commerce.payment.domain.model.PaymentStatus;

public class InvalidPaymentStateTransitionException extends ConflictException {

    public InvalidPaymentStateTransitionException(PaymentId id, PaymentStatus from, PaymentStatus to) {
        super("PAYMENT_INVALID_TRANSITION",
                "Transition invalide pour le paiement " + id.value() + " : " + from + " → " + to);
    }
}
