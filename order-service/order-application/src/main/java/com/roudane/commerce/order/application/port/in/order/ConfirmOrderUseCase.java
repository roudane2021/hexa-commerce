package com.roudane.commerce.order.application.port.in.order;


import java.util.UUID;

public interface ConfirmOrderUseCase {
    void handle(UUID orderId);
}
