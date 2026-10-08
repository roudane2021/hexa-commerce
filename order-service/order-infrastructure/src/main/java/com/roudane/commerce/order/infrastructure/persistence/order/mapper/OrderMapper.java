package com.roudane.commerce.order.infrastructure.persistence.order.mapper;

import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.OrderId;
import com.roudane.commerce.order.domain.model.OrderLine;
import com.roudane.commerce.order.domain.model.UserId;
import com.roudane.commerce.order.domain.model.discount.BulkQuantityDiscount;
import com.roudane.commerce.order.domain.model.discount.DiscountStrategy;
import com.roudane.commerce.order.domain.model.discount.PercentageDiscount;
import com.roudane.commerce.order.infrastructure.persistence.order.entity.OrderJpaEntity;
import com.roudane.commerce.order.infrastructure.persistence.order.entity.OrderLineJpaEntity;

import java.math.BigDecimal;
import java.util.List;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderJpaEntity toEntity(Order order) {

        final OrderJpaEntity entity = OrderJpaEntity.builder()
                .userId(order.getUserId().value())
                .status(order.getStatus())
                .build();

        order.getLines().stream()
                .map(OrderMapper::toLineEntity)
                .forEach(entity::addLine);

        return entity;
    }

    public static Order toDomain(OrderJpaEntity entity) {
        List<OrderLine> lines = entity.getLines().stream()
                .map(l -> new OrderLine(l.getProductId(), l.getQuantity(), l.getUnitPrice()))
                .toList();

        return new Order(
                new OrderId(entity.getId()),
                new UserId(entity.getUserId()),
                lines,
                entity.getCreatedAt(),
                entity.getStatus()
        );
    }

    private static OrderLineJpaEntity toLineEntity(OrderLine line) {
        DiscountStrategy strategy = line.getDiscountStrategy();

        return OrderLineJpaEntity.builder()
                .productId(line.getProductId())
                .quantity(line.getQuantity())
                .unitPrice(line.getUnitPrice())
                .discountType(toDiscountTypeJpa(strategy))
                .discountValue(extractDiscountValue(strategy))
                .discountThreshold(extractDiscountThreshold(strategy))
                .build();
    }

    private static OrderLineJpaEntity.DiscountTypeJpa toDiscountTypeJpa(DiscountStrategy strategy) {
        if (strategy == null) {
            return OrderLineJpaEntity.DiscountTypeJpa.NONE;
        }
        return switch (strategy) {
            case PercentageDiscount p -> OrderLineJpaEntity.DiscountTypeJpa.PERCENTAGE;
            case BulkQuantityDiscount b -> OrderLineJpaEntity.DiscountTypeJpa.BULK_QUANTITY;
            default -> OrderLineJpaEntity.DiscountTypeJpa.NONE;

        };
    }

    private static BigDecimal extractDiscountValue(DiscountStrategy strategy) {
        return switch (strategy) {
            case PercentageDiscount p -> p.percentage();
            case BulkQuantityDiscount b -> b.percentageAboveThreshold();
            default -> null;
        };
    }

    private static Integer extractDiscountThreshold(DiscountStrategy strategy) {
        return switch (strategy) {
            case BulkQuantityDiscount b -> b.thresholdQuantity();
            default -> null;
        };
    }
}