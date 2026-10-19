package com.roudane.commerce.order.domain.model.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Remise appliquée seulement à partir d'un seuil de quantité
public record BulkQuantityDiscount(int thresholdQuantity,
                                   BigDecimal percentageAboveThreshold) implements DiscountStrategy {

    @Override
    public BigDecimal apply(BigDecimal originalPrice, int quantity) {
        if (quantity < thresholdQuantity) {
            return originalPrice;
        }
        BigDecimal factor = BigDecimal.ONE.subtract(
                percentageAboveThreshold.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return originalPrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
