package com.roudane.commerce.common.messaging.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonEventSerializerAdapter implements EventSerializerPort {

    private final ObjectMapper objectMapper;

    public JacksonEventSerializerAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new IllegalStateException("Erreur de sérialisation de l'événement : " + event.getClass(), e);
        }
    }

    @Override
    public <T> T deserialize(String payload, Class<T> targetType) {
        try {
            return objectMapper.readValue(payload, targetType);
        } catch (Exception e) {
            throw new IllegalStateException("Erreur de désérialisation vers " + targetType.getSimpleName(), e);
        }
    }
}