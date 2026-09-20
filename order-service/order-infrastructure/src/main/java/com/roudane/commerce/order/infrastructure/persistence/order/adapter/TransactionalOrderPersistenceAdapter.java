package com.roudane.commerce.order.infrastructure.persistence.order.adapter;



import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.common.messaging.event.EventTopic;
import com.roudane.commerce.common.outbox.entities.OutboxEventJpaEntity;
import com.roudane.commerce.common.outbox.repository.OutboxEventJpaRepository;
import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.port.out.OrderPersistencePort;
import com.roudane.commerce.order.infrastructure.persistence.order.entity.OrderJpaEntity;
import com.roudane.commerce.order.infrastructure.persistence.order.mapper.OrderMapper;
import com.roudane.commerce.order.infrastructure.persistence.order.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class TransactionalOrderPersistenceAdapter implements OrderPersistencePort {

    private final OrderJpaRepository orderJpaRepository;
    private final OutboxEventJpaRepository outboxEventJpaRepository;

    public TransactionalOrderPersistenceAdapter(OrderJpaRepository orderJpaRepository,
                                                OutboxEventJpaRepository outboxEventJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
        this.outboxEventJpaRepository = outboxEventJpaRepository;
    }

    @Override
    @Transactional
    @LogTechnicalCall("Sauvegarde commande + événement outbox")
    public Order createAndEnqueueEvent(Order order, String eventPayloadJson) {
        OrderJpaEntity savedEntity = orderJpaRepository.save(OrderMapper.toEntity(order));

        OutboxEventJpaEntity outboxEvent = new OutboxEventJpaEntity(
                UUID.randomUUID(),
                EventTopic.ORDER_CREATED.getName(),
                savedEntity.getId().toString(),
                eventPayloadJson
        );
        outboxEventJpaRepository.save(outboxEvent);

        return OrderMapper.toDomain(savedEntity);
    }
}
