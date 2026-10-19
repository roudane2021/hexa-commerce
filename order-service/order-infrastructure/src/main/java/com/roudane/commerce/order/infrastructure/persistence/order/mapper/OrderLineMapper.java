package com.roudane.commerce.order.infrastructure.persistence.order.mapper;

import com.roudane.commerce.order.domain.model.OrderLine;
import com.roudane.commerce.order.domain.model.discount.BulkQuantityDiscount;
import com.roudane.commerce.order.domain.model.discount.DiscountStrategy;
import com.roudane.commerce.order.domain.model.discount.NoDiscount;
import com.roudane.commerce.order.domain.model.discount.PercentageDiscount;
import com.roudane.commerce.order.infrastructure.persistence.order.entity.OrderLineJpaEntity;

import java.math.BigDecimal;

public class OrderLineMapper {


    public static OrderLineJpaEntity toEntity(OrderLine line) {
        DiscountStrategy s = line.getDiscountStrategy();

        // 1. Définition des valeurs spécifiques à la stratégie via le pattern matching
        DiscountDetails details = switch (s) {
            case PercentageDiscount p -> new DiscountDetails(
                    OrderLineJpaEntity.DiscountTypeJpa.PERCENTAGE,
                    p.percentage(),
                    null
            );
            case BulkQuantityDiscount b -> new DiscountDetails(
                    OrderLineJpaEntity.DiscountTypeJpa.BULK_QUANTITY,
                    b.percentageAboveThreshold(),
                    b.thresholdQuantity()
            );
            case NoDiscount ignored -> new DiscountDetails(
                    OrderLineJpaEntity.DiscountTypeJpa.NONE,
                    null,
                    null
            );
            default -> throw new IllegalStateException("Stratégie de remise non gérée : " + s.getClass());
        };

        // 2. Construction de l'entité avec le Builder de Lombok
        return OrderLineJpaEntity.builder()
                .productId(line.getProductId())
                .quantity(line.getQuantity())
                .unitPrice(line.getUnitPrice())
                .discountType(details.type())
                .discountValue(details.value())
                .discountThreshold(details.threshold())
                .build();
    }

    public static OrderLine toDomain(OrderLineJpaEntity entity) {
        DiscountStrategy strategy = switch (entity.getDiscountType()) {
            case PERCENTAGE -> new PercentageDiscount(entity.getDiscountValue());
            case BULK_QUANTITY -> new BulkQuantityDiscount(
                    entity.getDiscountThreshold(), entity.getDiscountValue());
            case NONE -> new NoDiscount();
        };
        return new OrderLine(entity.getProductId(), entity.getQuantity(),
                entity.getUnitPrice(), strategy);
    }

    private record DiscountDetails(
            OrderLineJpaEntity.DiscountTypeJpa type,
            BigDecimal value,
            Integer threshold
    ) {
    }
}
