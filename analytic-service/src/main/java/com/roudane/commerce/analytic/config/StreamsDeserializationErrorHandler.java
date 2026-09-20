package com.roudane.commerce.analytic.config;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.errors.ErrorHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StreamsDeserializationErrorHandler implements DeserializationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(StreamsDeserializationErrorHandler.class);

    @Override
    public DeserializationHandlerResponse handle(ErrorHandlerContext context,
                                                 ConsumerRecord<byte[], byte[]> record,
                                                 Exception exception) {
        log.error("Erreur de désérialisation sur topic={} partition={} offset={} : {}",
                record.topic(), record.partition(), record.offset(), exception.getMessage());

        // CONTINUE : ignore ce message précis et poursuit le traitement des suivants
        // (alternative : FAIL, qui arrête toute la topology — à éviter sauf cas critique)
        return DeserializationHandlerResponse.CONTINUE;
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // rien à configurer ici pour ce cas simple
    }
}
