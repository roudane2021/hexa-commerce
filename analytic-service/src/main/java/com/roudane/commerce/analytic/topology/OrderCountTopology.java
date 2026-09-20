package com.roudane.commerce.analytic.topology;


import com.roudane.commerce.common.messaging.event.EventTopic;
import com.roudane.commerce.common.messaging.event.OrderCreatedEvent;
import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderCountTopology {

    private final EventSerializerPort eventSerializerPort;

    public OrderCountTopology(final EventSerializerPort eventSerializerPort) {
        this.eventSerializerPort = eventSerializerPort;
    }

    @Bean
    public KStream<String, String> orderCountByUserStream(StreamsBuilder streamsBuilder) {

        KStream<String, String> orders = streamsBuilder.stream(
                EventTopic.ORDER_CREATED.getName(), Consumed.with(Serdes.String(), Serdes.String()));


        // Reclé le flux par userId (au lieu de orderId), puis compte les occurrences
        KTable<String, Long> countByUser = orders
                .map((key, value) -> {
                    OrderCreatedEvent event = eventSerializerPort.deserialize(value, OrderCreatedEvent.class);
                    return KeyValue.pair(event.userId().toString(), value);
                })
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                .count(Materialized.as("order-count-by-user-store")); // stocké dans un state store nommé

        // Publie le résultat agrégé sur un nouveau topic, consultable ailleurs
        countByUser.toStream().to(EventTopic.ORDER_COUNT_BY_USER,
                Produced.with(Serdes.String(), Serdes.Long()));

        return orders;
    }

}
