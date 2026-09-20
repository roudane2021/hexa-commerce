package com.roudane.commerce.common.outbox.entities;

import com.roudane.commerce.common.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventJpaEntity extends BaseJpaEntity {

    @Column(nullable = false)
    private String topic;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean published = false;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    protected OutboxEventJpaEntity() {
    }

    public OutboxEventJpaEntity(UUID id, String topic, String aggregateId, String payload) {
        super(id);
        this.topic = topic;
        this.aggregateId = aggregateId;
        this.payload = payload;
    }

    public void markPublished() { this.published = true; }
    public void incrementRetry() { this.retryCount++; }

    public String getTopic() { return topic; }
    public String getAggregateId() { return aggregateId; }
    public String getPayload() { return payload; }
    public boolean isPublished() { return published; }
    public int getRetryCount() { return retryCount; }
}