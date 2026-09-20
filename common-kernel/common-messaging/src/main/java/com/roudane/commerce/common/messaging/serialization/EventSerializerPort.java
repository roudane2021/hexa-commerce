package com.roudane.commerce.common.messaging.serialization;

public interface EventSerializerPort {

    String serialize(Object event);

    <T> T deserialize(String payload, Class<T> targetType);
}