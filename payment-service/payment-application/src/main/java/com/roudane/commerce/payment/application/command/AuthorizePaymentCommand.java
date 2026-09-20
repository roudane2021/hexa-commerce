package com.roudane.commerce.payment.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record AuthorizePaymentCommand(UUID orderId, BigDecimal amount) {
}
