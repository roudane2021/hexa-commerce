package com.roudane.commerce.order.application.port.in.order;


import java.util.UUID;

public interface CancelOrderUseCase {
    void handle(UUID orderId);
}
