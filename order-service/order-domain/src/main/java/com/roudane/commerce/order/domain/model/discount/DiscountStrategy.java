package com.roudane.commerce.order.domain.model.discount;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal apply(BigDecimal originalPrice, int quantity);
}
