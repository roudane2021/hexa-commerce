package com.roudane.commerce.order.infrastructure.persistence.order.repository;

import com.roudane.commerce.order.infrastructure.persistence.order.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"lines"})
    List<OrderJpaEntity> findByUserId(UUID userId);

    @Query("SELECT o FROM OrderJpaEntity o LEFT JOIN FETCH o.lines WHERE o.id = :id")
    Optional<OrderJpaEntity> findByIdWithLines(@Param("id") UUID id);
}
