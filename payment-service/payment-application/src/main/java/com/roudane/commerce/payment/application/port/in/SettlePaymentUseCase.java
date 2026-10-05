package com.roudane.commerce.payment.application.port.in;

import java.util.UUID;


public interface SettlePaymentUseCase {
    void handle(UUID orderId);
}
