package com.roudane.commerce.order.infrastructure.persistence.order.repository;

import com.roudane.commerce.order.infrastructure.persistence.order.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"lines"})
    List<OrderJpaEntity> findByUserId(UUID userId);
}
