package com.roudane.commerce.order.config.beans;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import com.roudane.commerce.common.messaging.serialization.JacksonEventSerializerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Enregistre le module pour gérer java.time.Instant, LocalDateTime, etc.
        objectMapper.registerModule(new JavaTimeModule());

        // Sérialise au format String ISO-8601 (ex: "2026-09-24T13:28:26Z")
        // au lieu d'un tableau/timestamp numérique
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }

    @Bean
    public EventSerializerPort eventSerializerPort(ObjectMapper objectMapper) {
        return new JacksonEventSerializerAdapter(objectMapper);
    }
}
