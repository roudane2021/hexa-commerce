package com.roudane.commerce.order.domain.model.discount;

import com.roudane.commerce.order.domain.exception.InvalidOrderLineException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PercentageDiscount(BigDecimal percentage) implements DiscountStrategy {

    public PercentageDiscount {
        if (percentage == null || percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidOrderLineException("Percentage must be between 0 and 100");
        }
    }

    @Override
    public BigDecimal apply(BigDecimal originalPrice, int quantity) {
        BigDecimal factor = BigDecimal.ONE.subtract(percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return originalPrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
