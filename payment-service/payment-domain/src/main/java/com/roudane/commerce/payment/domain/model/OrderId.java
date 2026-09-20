package com.roudane.commerce.payment.domain.model;

import java.util.UUID;

// Value object PROPRE à payment-domain — ne dépend jamais de order-domain.
// Bounded context séparé : même concept métier, classe indépendante.
public record OrderId(UUID value) {
}