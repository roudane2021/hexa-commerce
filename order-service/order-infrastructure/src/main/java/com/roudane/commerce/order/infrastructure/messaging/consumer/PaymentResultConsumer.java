package com.roudane.commerce.order.infrastructure.messaging.consumer;



import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.common.messaging.event.EventTopic;
import com.roudane.commerce.common.messaging.event.PaymentFailedEvent;
import com.roudane.commerce.common.messaging.event.PaymentValidatedEvent;
import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import com.roudane.commerce.order.application.port.in.order.CancelOrderUseCase;
import com.roudane.commerce.order.application.port.in.order.ConfirmOrderUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);

    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final EventSerializerPort eventSerializerPort;

    public PaymentResultConsumer(ConfirmOrderUseCase confirmOrderUseCase,
                                 CancelOrderUseCase cancelOrderUseCase,
                                 EventSerializerPort eventSerializerPort) {
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.eventSerializerPort = eventSerializerPort;
    }

    @LogTechnicalCall("Consommation payment.validated")
    @KafkaListener(
            topics = EventTopic.PAYMENT_VALIDATED_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onPaymentValidated(String rawPayload,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                   @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
                                   @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {

        PaymentValidatedEvent event = eventSerializerPort.deserialize(rawPayload, PaymentValidatedEvent.class);
        log.info("Événement payment.validated reçu [key={}, partition={}, offset={}, orderId={}]",
                key, partition, offset, event.orderId());

        confirmOrderUseCase.handle(event.orderId());
    }

    @LogTechnicalCall("Consommation payment.failed")
    @KafkaListener(topics = EventTopic.PAYMENT_FAILED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentFailed(String rawPayload,
                                @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
                                @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {

        PaymentFailedEvent event = eventSerializerPort.deserialize(rawPayload, PaymentFailedEvent.class);

        log.info("Événement payment.Failed reçu [key={}, partition={}, offset={}, orderId={}]",
                key, partition, offset, event.orderId());
            cancelOrderUseCase.handle(event.orderId());

    }
}
