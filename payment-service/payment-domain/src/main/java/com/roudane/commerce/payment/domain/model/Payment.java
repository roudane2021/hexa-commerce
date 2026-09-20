package com.roudane.commerce.payment.domain.model;

import com.roudane.commerce.payment.domain.exception.InvalidPaymentAmountException;
import com.roudane.commerce.payment.domain.exception.InvalidPaymentStateTransitionException;
import com.roudane.commerce.payment.domain.exception.PaymentAlreadySettledException;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {

    private final PaymentId id;
    private final OrderId orderId;
    private final BigDecimal amount;
    private final Instant createdAt;
    private PaymentStatus status;

    public Payment(PaymentId id, OrderId orderId, BigDecimal amount, Instant createdAt, PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.status = status;
    }

    // Création initiale, avant autorisation — RG n°1 : montant strictement positif
    public static Payment initiate(OrderId orderId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidPaymentAmountException("Le montant du paiement doit être strictement positif : " + amount);
        }
        return new Payment(PaymentId.generate(), orderId, amount, Instant.now(), PaymentStatus.PENDING);
    }

    // Transition SYNCHRONE (étape 1 du scénario) : autorisation rapide type carte bancaire
    public void authorize() {
        assertTransitionAllowed(PaymentStatus.PENDING, PaymentStatus.AUTHORIZED);
        this.status = PaymentStatus.AUTHORIZED;
    }

    public void reject() {
        assertTransitionAllowed(PaymentStatus.PENDING, PaymentStatus.REJECTED);
        this.status = PaymentStatus.REJECTED;
    }

    // Transition ASYNCHRONE (étape 2 du scénario) : règlement définitif, déclenché par order.created consumer
    public void settle() {
        if (this.status == PaymentStatus.SETTLED) {
            throw new PaymentAlreadySettledException(this.id); // RG n°2 : idempotence au niveau domaine
        }
        assertTransitionAllowed(PaymentStatus.AUTHORIZED, PaymentStatus.SETTLED);
        this.status = PaymentStatus.SETTLED;
    }

    public void fail() {
        assertTransitionAllowed(PaymentStatus.AUTHORIZED, PaymentStatus.FAILED);
        this.status = PaymentStatus.FAILED;
    }

    // RG n°3 : transitions d'état contrôlées, aucun saut d'état arbitraire autorisé
    private void assertTransitionAllowed(PaymentStatus expectedCurrent, PaymentStatus target) {
        if (this.status != expectedCurrent) {
            throw new InvalidPaymentStateTransitionException(this.id, this.status, target);
        }
    }

    public PaymentId getId() { return id; }
    public OrderId getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
    public PaymentStatus getStatus() { return status; }
}
