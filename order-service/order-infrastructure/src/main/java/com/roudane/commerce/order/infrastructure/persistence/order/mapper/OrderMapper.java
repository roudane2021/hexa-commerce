package com.roudane.commerce.order.infrastructure.persistence.order.mapper;

import com.roudane.commerce.order.domain.model.*;
import com.roudane.commerce.order.infrastructure.persistence.order.entity.OrderJpaEntity;
import com.roudane.commerce.order.infrastructure.persistence.order.entity.OrderLineJpaEntity;

import java.util.List;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
                order.getId().value(),
                order.getUserId().value(),
                order.getStatus()
        );

        order.getLines().forEach(line ->
                entity.addLine(new OrderLineJpaEntity(
                        line.getProductId(),
                        line.getQuantity(),
                        line.getUnitPrice()
                ))
        );

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
}