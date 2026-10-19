package com.roudane.commerce.order.domain.model.discount;

import java.math.BigDecimal;

public record NoDiscount() implements DiscountStrategy {
    @Override
    public BigDecimal apply(BigDecimal originalPrice, int quantity) {
        return originalPrice;
    }
}