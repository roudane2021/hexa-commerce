package com.roudane.commerce.order.domain.model;

import com.roudane.commerce.order.domain.exception.EmptyOrderException;
import com.roudane.commerce.order.domain.exception.OrderAlreadyShippedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Order {

    private final OrderId id;
    private final UserId userId;
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
        if (lines == null || lines.isEmpty()) {
            throw new EmptyOrderException();
        }
        return new Order(OrderId.generate(), userId, lines, Instant.now(), OrderStatus.CREATED);
    }

    public BigDecimal totalAmount() {
        return lines.stream()
                .map(OrderLine::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalSavings() {
        return lines.stream()
                .map(line -> line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity()))
                        .subtract(line.subtotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, List<OrderLine>> groupLinesByPriceRange() {
        return lines.stream()
                .collect(Collectors.groupingBy(line -> {
                    if (line.getUnitPrice().compareTo(BigDecimal.valueOf(50)) < 0) return "ÉCONOMIQUE";
                    if (line.getUnitPrice().compareTo(BigDecimal.valueOf(200)) < 0) return "STANDARD";
                    return "PREMIUM";
                }));
    }
    

    // ---- Transitions liées au paiement SYNCHRONE (autorisation rapide) ----

    public void markPaymentPending() {
        this.status = OrderStatus.PAYMENT_PENDING;
    }

    public void markPaymentRejected() {
        this.status = OrderStatus.PAYMENT_REJECTED;
    }

    // ---- Transitions liées au règlement ASYNCHRONE (via Kafka) ----

    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new OrderAlreadyShippedException(this.id);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public OrderId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }
}