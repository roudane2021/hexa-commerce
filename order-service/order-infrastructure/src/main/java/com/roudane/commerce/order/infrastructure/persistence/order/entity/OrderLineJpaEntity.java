package com.roudane.commerce.order.infrastructure.persistence.order.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_lines")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderLineJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountTypeJpa discountType;
    @Column(name = "discount_value")
    private BigDecimal discountValue;
    @Column(name = "discount_threshold")
    private Integer discountThreshold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;


    @Builder
    public OrderLineJpaEntity(String productId, int quantity,
                              BigDecimal unitPrice, DiscountTypeJpa discountType,
                              BigDecimal discountValue, Integer discountThreshold) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.discountThreshold = discountThreshold;
    }

    public enum DiscountTypeJpa {NONE, PERCENTAGE, BULK_QUANTITY}

    void attachTo(OrderJpaEntity order) {
        this.order = order;
    }

    public UUID getId() {
        return id;
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

    public DiscountTypeJpa getDiscountType() {
        return discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public Integer getDiscountThreshold() {
        return discountThreshold;
    }
}