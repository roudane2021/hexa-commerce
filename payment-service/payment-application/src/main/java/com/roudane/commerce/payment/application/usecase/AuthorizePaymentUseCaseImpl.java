package com.roudane.commerce.payment.application.usecase;


import com.roudane.commerce.common.annotation.LogBusinessAction;
import com.roudane.commerce.payment.application.command.AuthorizePaymentCommand;
import com.roudane.commerce.payment.application.port.in.AuthorizePaymentUseCase;
import com.roudane.commerce.payment.domain.model.OrderId;
import com.roudane.commerce.payment.domain.model.Payment;
import com.roudane.commerce.payment.domain.port.out.PaymentRepositoryPort;

import java.math.BigDecimal;

public class AuthorizePaymentUseCaseImpl implements AuthorizePaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;

    public AuthorizePaymentUseCaseImpl(PaymentRepositoryPort paymentRepositoryPort) {
        this.paymentRepositoryPort = paymentRepositoryPort;
    }

    @Override
    @LogBusinessAction("AuthorizePayment")
    public Payment handle(AuthorizePaymentCommand command) {
        OrderId orderId = new OrderId(command.orderId());

        Payment payment = paymentRepositoryPort.findByOrderId(orderId)
                .orElseGet(() -> Payment.initiate(orderId, command.amount()));

        // Simulation de la vérification rapide (carte valide, plafond, etc.)
        boolean accepted = simulateQuickAuthorization(command.amount());

        if (accepted) {
            payment.authorize();
        } else {
            payment.reject();
        }

        return paymentRepositoryPort.save(payment);
    }

    private boolean simulateQuickAuthorization(BigDecimal amount) {
        // Règle simple de démonstration : refuse au-delà d'un plafond arbitraire
        return amount.compareTo(new java.math.BigDecimal("10000")) < 0;
    }
}
