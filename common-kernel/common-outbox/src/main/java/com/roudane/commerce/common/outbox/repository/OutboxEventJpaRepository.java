package com.roudane.commerce.common.outbox.repository;


import com.roudane.commerce.common.outbox.entities.OutboxEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
    List<OutboxEventJpaEntity> findTop50ByPublishedFalseOrderByCreatedAtAsc();
}
