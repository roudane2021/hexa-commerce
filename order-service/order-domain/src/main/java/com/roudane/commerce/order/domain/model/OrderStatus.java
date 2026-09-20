package com.roudane.commerce.order.domain.model;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAYMENT_REJECTED,
    CONFIRMED,
    CANCELLED,
    SHIPPED
}
