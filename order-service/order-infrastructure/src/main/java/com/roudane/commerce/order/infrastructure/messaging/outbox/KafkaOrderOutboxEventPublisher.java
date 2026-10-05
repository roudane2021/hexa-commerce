package com.roudane.commerce.order.infrastructure.messaging.outbox;


import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.common.avro.converter.AvroEventConverterRegistry;
import com.roudane.commerce.common.outbox.AbstractOutboxEventPublisher;
import com.roudane.commerce.common.outbox.entities.OutboxEventJpaEntity;
import com.roudane.commerce.common.outbox.repository.OutboxEventJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderOutboxEventPublisher extends AbstractOutboxEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AvroEventConverterRegistry converterRegistry;

    public KafkaOrderOutboxEventPublisher(OutboxEventJpaRepository outboxRepository,
                                          KafkaTemplate<String, Object> kafkaTemplate,
                                          AvroEventConverterRegistry converterRegistry,
                                          @Value("${outbox.max-retry:5}") int maxRetry) {
        super(outboxRepository, maxRetry);
        this.kafkaTemplate = kafkaTemplate;
        this.converterRegistry = converterRegistry;
    }

    @Override
    @LogTechnicalCall("Publication événement outbox (order)")
    protected void doPublish(OutboxEventJpaEntity event) throws Exception {
        Object avroEvent = converterRegistry.convert(event.getTopic(), event.getPayload());
        kafkaTemplate.send(event.getTopic(), event.getAggregateId(), avroEvent).get();
    }

}
