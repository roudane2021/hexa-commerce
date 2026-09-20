package com.roudane.commerce.order.domain.port.out;

import com.roudane.commerce.order.domain.model.Order;

public interface OrderPersistencePort {
    // Sauvegarde l'Order ET enregistre l'événement à publier, de façon atomique
    Order createAndEnqueueEvent(Order order, String eventPayloadJson);
}
