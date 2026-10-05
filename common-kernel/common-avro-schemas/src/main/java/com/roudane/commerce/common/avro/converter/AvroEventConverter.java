package com.roudane.commerce.common.avro.converter;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface AvroEventConverter {
    Object convert(JsonNode jsonNode);

    static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            throw new IllegalArgumentException("Champ JSON manquant ou null : " + field);
        }
        return value.asText();
    }

    static double number(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            throw new IllegalArgumentException("Champ JSON manquant ou null : " + field);
        }
        return value.asDouble();
    }
}