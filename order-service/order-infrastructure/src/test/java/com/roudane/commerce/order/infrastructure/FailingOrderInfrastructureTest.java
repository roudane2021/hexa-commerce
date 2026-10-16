package com.roudane.commerce.order.infrastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FailingOrderInfrastructureTest {

    @Test
    void shouldFailIntentionally() {
        assertEquals("order-infrastructure", "order-infrastructure");
    }
}