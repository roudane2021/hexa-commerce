package com.roudane.commerce.payment.infrastructure.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roudane.commerce.common.avro.PaymentFailedEvent;
import com.roudane.commerce.common.avro.PaymentValidatedEvent;
import com.roudane.commerce.common.avro.converter.AvroEventConverterRegistry;
import com.roudane.commerce.common.messaging.event.EventTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.roudane.commerce.common.avro.converter.AvroEventConverter.number;
import static com.roudane.commerce.common.avro.converter.AvroEventConverter.text;

@Configuration
public class PaymentAvroConverters {

    @Bean
    public AvroEventConverterRegistry paymentAvroEventConverterRegistry(ObjectMapper objectMapper) {
        return new AvroEventConverterRegistry(objectMapper)
                .register(EventTopic.PAYMENT_VALIDATED.getName(), node -> PaymentValidatedEvent.newBuilder()
                        .setOrderId(text(node, "orderId"))
                        .setPaymentId(text(node, "paymentId"))
                        .setAmount(number(node, "amount"))
                        .setOccurredAt(text(node, "occurredAt"))
                        .build())

                .register(EventTopic.PAYMENT_FAILED.getName(), node -> PaymentFailedEvent.newBuilder()
                        .setOrderId(text(node, "orderId"))
                        .setPaymentId(text(node, "paymentId"))
                        .setAmount(number(node, "amount"))
                        .setOccurredAt(text(node, "occurredAt"))
                        .setReason(text(node, "reason"))
                        .build());
    }
}
