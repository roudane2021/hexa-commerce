package com.roudane.commerce.payment.infrastructure.messaging.consumer;


import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.common.messaging.event.EventTopic;
import com.roudane.commerce.common.messaging.event.OrderCreatedEvent;
import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import com.roudane.commerce.payment.application.port.in.SettlePaymentUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private final SettlePaymentUseCase settlePaymentUseCase;
    private final EventSerializerPort eventSerializerPort;

    public OrderCreatedConsumer(SettlePaymentUseCase settlePaymentUseCase, EventSerializerPort eventSerializerPort) {
        this.settlePaymentUseCase = settlePaymentUseCase;
        this.eventSerializerPort = eventSerializerPort;
    }

    @LogTechnicalCall("Consommation order.created")
    @KafkaListener(topics = EventTopic.ORDER_CREATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderCreated(String rawPayload) {
        OrderCreatedEvent event = eventSerializerPort.deserialize(rawPayload, OrderCreatedEvent.class);
        settlePaymentUseCase.handle(event.orderId());
    }
}
