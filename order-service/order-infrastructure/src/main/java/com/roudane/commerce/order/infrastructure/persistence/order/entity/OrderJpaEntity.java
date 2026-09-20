package com.roudane.commerce.order.infrastructure.persistence.order.entity;


import com.roudane.commerce.common.persistence.BaseJpaEntity;
import com.roudane.commerce.order.domain.model.OrderStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity extends BaseJpaEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderLineJpaEntity> lines = new ArrayList<>();

    protected OrderJpaEntity() {
        // requis par JPA
    }

    public OrderJpaEntity(UUID id, UUID userId, OrderStatus status) {
        super(id);
        this.userId = userId;
        this.status = status;
    }

    public void addLine(OrderLineJpaEntity line) {
        line.attachTo(this);
        this.lines.add(line);
    }

    public UUID getUserId() { return userId; }
    public OrderStatus getStatus() { return status; }
    public List<OrderLineJpaEntity> getLines() { return lines; }
}