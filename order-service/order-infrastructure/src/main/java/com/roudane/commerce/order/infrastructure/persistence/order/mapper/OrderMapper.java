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
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    public static final OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    // ---------- Domaine -> Entité ----------

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "lines", ignore = true)
    public abstract OrderJpaEntity toEntity(Order order);

    @Mapping(target = "discountType", source = "discountStrategy", qualifiedByName = "discountType")
    @Mapping(target = "discountValue", source = "discountStrategy", qualifiedByName = "discountValue")
    @Mapping(target = "discountThreshold", source = "discountStrategy", qualifiedByName = "discountThreshold")
    protected abstract OrderLineJpaEntity toLineEntity(OrderLine line);

    // Remplace entity::addLine : attache chaque ligne à son parent (order_id)
    @AfterMapping
    protected void attachLines(Order order, @MappingTarget OrderJpaEntity entity) {
        order.getLines().stream()
                .map(this::toLineEntity)
                .forEach(entity::addLine);
    }

    // ---------- Entité -> Domaine ----------

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    public abstract Order toDomain(OrderJpaEntity entity);

    protected OrderLine toDomainLine(OrderLineJpaEntity l) {
        return new OrderLine(l.getProductId(), l.getQuantity(), l.getUnitPrice() /*, toStrategy(l) */);
    }

    protected OrderId toOrderId(UUID id) {
        return id == null ? null : new OrderId(id);
    }

    protected UserId toUserId(UUID id) {
        return id == null ? null : new UserId(id);
    }

    // ---------- Stratégies de remise ----------

    @Named("discountType")
    protected OrderLineJpaEntity.DiscountTypeJpa toDiscountType(DiscountStrategy s) {
        return switch (s) {
            case null -> OrderLineJpaEntity.DiscountTypeJpa.NONE;
            case PercentageDiscount p -> OrderLineJpaEntity.DiscountTypeJpa.PERCENTAGE;
            case BulkQuantityDiscount b -> OrderLineJpaEntity.DiscountTypeJpa.BULK_QUANTITY;
            default -> OrderLineJpaEntity.DiscountTypeJpa.NONE;
        };
    }

    @Named("discountValue")
    protected BigDecimal toDiscountValue(DiscountStrategy s) {
        return switch (s) {
            case PercentageDiscount p -> p.percentage();
            case BulkQuantityDiscount b -> b.percentageAboveThreshold();
            case null, default -> null;
        };
    }

    @Named("discountThreshold")
    protected Integer toDiscountThreshold(DiscountStrategy s) {
        return switch (s) {
            case BulkQuantityDiscount b -> b.thresholdQuantity();
            case null, default -> null;
        };
    }
}