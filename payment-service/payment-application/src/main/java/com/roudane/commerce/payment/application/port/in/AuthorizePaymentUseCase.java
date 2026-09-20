package com.roudane.commerce.payment.application.port.in;

import com.roudane.commerce.payment.application.command.AuthorizePaymentCommand;
import com.roudane.commerce.payment.domain.model.Payment;

public interface AuthorizePaymentUseCase {
    Payment handle(AuthorizePaymentCommand command);
}
