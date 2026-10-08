package com.roudane.commerce.order.domain.model;

import com.roudane.commerce.order.domain.exception.InvalidOrderLineException;
import com.roudane.commerce.order.domain.model.discount.DiscountStrategy;
import com.roudane.commerce.order.domain.model.discount.NoDiscount;

import java.math.BigDecimal;

public class OrderLine {

    private final String productId;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final DiscountStrategy discountStrategy;

    public OrderLine(String productId, int quantity, BigDecimal unitPrice) {
        this(productId, quantity, unitPrice, new NoDiscount());
    }

    public OrderLine(String productId, int quantity, BigDecimal unitPrice, DiscountStrategy discountStrategy) {
        // RG : une ligne doit avoir une quantité positive
        if (quantity <= 0) {
            throw new InvalidOrderLineException("La quantité doit être positive : " + quantity);
        }
        // RG : le prix ne peut pas être négatif
        if (unitPrice.signum() < 0) {
            throw new InvalidOrderLineException("Le prix ne peut pas être négatif");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountStrategy = discountStrategy != null ? discountStrategy : new NoDiscount();
    }


    // RG : calcul du sous-total de la ligne
    public BigDecimal subtotal() {
        final BigDecimal discountedUnitPrice = discountStrategy.apply(unitPrice, quantity);
        return discountedUnitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal savings() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity))
                .subtract(subtotal());
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public DiscountStrategy getDiscountStrategy() {
        return discountStrategy;
    }
}