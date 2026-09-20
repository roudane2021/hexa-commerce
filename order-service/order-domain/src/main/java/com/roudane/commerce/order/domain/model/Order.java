package com.roudane.commerce.order.domain.model;

import com.roudane.commerce.order.domain.exception.EmptyOrderException;
import com.roudane.commerce.order.domain.exception.OrderAlreadyShippedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Order {

    private final OrderId id;
    private final UserId userId;          // référence par ID, pas l'objet User complet
    private final List<OrderLine> lines;
    private final Instant createdAt;
    private OrderStatus status;

    public Order(OrderId id, UserId userId, List<OrderLine> lines, Instant createdAt, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.lines = lines;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static Order create(UserId userId, List<OrderLine> lines) {
        // RG n°1 : une commande doit contenir au moins une ligne
        if (lines == null || lines.isEmpty()) {
            throw new EmptyOrderException();
        }
        return new Order(OrderId.generate(), userId, lines, Instant.now(), OrderStatus.CREATED);
    }

    // RG n°2 : le total est calculé, jamais stocké en dur (évite les incohérences)
    public BigDecimal totalAmount() {
        return lines.stream()
                .map(OrderLine::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // RG n°3 : on ne peut annuler une commande déjà expédiée
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new OrderAlreadyShippedException(this.id);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public OrderId getId() { return id; }
    public UserId getUserId() { return userId; }
    public List<OrderLine> getLines() { return lines; }
    public Instant getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
}