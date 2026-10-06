package com.roudane.commerce.order.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roudane.commerce.common.avro.OrderCreatedEvent;
import com.roudane.commerce.common.avro.converter.AvroEventConverterRegistry;
import com.roudane.commerce.common.messaging.event.EventTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.roudane.commerce.common.avro.converter.AvroEventConverter.number;
import static com.roudane.commerce.common.avro.converter.AvroEventConverter.text;

@Configuration
public class OrderAvroConverters {

    @Bean
    public AvroEventConverterRegistry orderAvroEventConverterRegistry(ObjectMapper objectMapper) {
        return new AvroEventConverterRegistry(objectMapper)
                .register(EventTopic.ORDER_CREATED.getName(), node -> OrderCreatedEvent.newBuilder()
                        .setOrderId(text(node, "orderId"))
                        .setUserId(text(node, "userId"))
                        .setTotalAmount(number(node, "totalAmount"))
                        .setOccurredAt(text(node, "occurredAt"))
                        .build());
    }
}
