package com.roudane.commerce.payment.infrastructure.persistence.payment.entities;


import com.roudane.commerce.common.persistence.BaseJpaEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentJpaEntity extends BaseJpaEntity {

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    //@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String status;

    protected PaymentJpaEntity() {
    }

    public PaymentJpaEntity(UUID id, UUID orderId, BigDecimal amount, String status) {
        super(id);
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public UUID getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
