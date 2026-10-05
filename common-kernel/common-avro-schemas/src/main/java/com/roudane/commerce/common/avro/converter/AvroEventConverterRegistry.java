package com.roudane.commerce.common.avro.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AvroEventConverterRegistry {

    private final Map<String, AvroEventConverter> converters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public AvroEventConverterRegistry(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AvroEventConverterRegistry register(String topic, AvroEventConverter converter) {
        converters.put(topic, converter);
        return this;
    }

    public Object convert(String topic, String jsonPayload) throws Exception {
        AvroEventConverter converter = converters.get(topic);
        if (converter == null) {
            throw new IllegalArgumentException("Aucun convertisseur Avro enregistré pour le topic : " + topic);
        }
        JsonNode node = objectMapper.readTree(jsonPayload);
        return converter.convert(node);
    }
}
