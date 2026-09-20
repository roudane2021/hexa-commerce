package com.roudane.commerce.order.infrastructure.messaging.outbox;



import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.common.outbox.AbstractOutboxEventPublisher;
import com.roudane.commerce.common.outbox.entities.OutboxEventJpaEntity;
import com.roudane.commerce.common.outbox.repository.OutboxEventJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderOutboxEventPublisher extends AbstractOutboxEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaOrderOutboxEventPublisher(OutboxEventJpaRepository outboxRepository,
                                          KafkaTemplate<String, String> kafkaTemplate,
                                          @Value("${outbox.max-retry:5}") int maxRetry) {
        super(outboxRepository, maxRetry);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @LogTechnicalCall("Publication événement outbox (order)")
    protected void doPublish(OutboxEventJpaEntity event) throws Exception {
        kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload()).get();
    }
}
