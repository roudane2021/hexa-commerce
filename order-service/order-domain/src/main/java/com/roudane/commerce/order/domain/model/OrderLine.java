package com.roudane.commerce.order.domain.model;

import com.roudane.commerce.order.domain.exception.InvalidOrderLineException;

import java.math.BigDecimal;

public class OrderLine {

    private final String productId;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderLine(String productId, int quantity, BigDecimal unitPrice) {
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
    }

    // RG : calcul du sous-total de la ligne
    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}