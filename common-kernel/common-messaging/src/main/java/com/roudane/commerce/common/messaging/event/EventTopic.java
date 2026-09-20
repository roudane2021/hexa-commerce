package com.roudane.commerce.common.messaging.event;


public enum EventTopic {

    ORDER_CREATED("order.created"),
    PAYMENT_VALIDATED("payment.validated"),
    PAYMENT_FAILED("payment.failed");

    public static final String ORDER_CREATED_TOPIC = "order.created";
    public static final String PAYMENT_VALIDATED_TOPIC = "payment.validated";
    public static final String PAYMENT_FAILED_TOPIC = "payment.failed";

    public static final String ORDER_COUNT_BY_USER = "order.count.by-user";
    public static final String REVENUE_PER_MINUTE = "revenue.per-minute";
    public static final String ORDER_PAYMENT_DELAY = "order.payment.delay";

    private final String name;

    EventTopic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}