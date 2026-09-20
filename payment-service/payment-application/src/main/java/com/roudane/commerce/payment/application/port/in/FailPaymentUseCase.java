package com.roudane.commerce.payment.application.port.in;

import java.util.UUID;


public interface FailPaymentUseCase {
    void handle(UUID orderId);
}
