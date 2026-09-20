package com.roudane.commerce.analytic.topology;

import com.roudane.commerce.common.messaging.event.EventTopic;
import com.roudane.commerce.common.messaging.event.OrderCreatedEvent;
import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Duration;

@Configuration
public class RevenueAggregationTopology {

    private final EventSerializerPort eventSerializerPort;

    public RevenueAggregationTopology(final EventSerializerPort eventSerializerPort) {
        this.eventSerializerPort = eventSerializerPort;
    }

    @Bean
    public KStream<String, String> revenueWindowedStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> orders = streamsBuilder.stream(
                EventTopic.ORDER_CREATED_TOPIC, Consumed.with(Serdes.String(), Serdes.String()));

        KTable<Windowed<String>, BigDecimal> revenuePerMinute = orders
                .map((key, value) -> KeyValue.pair("global", value)) // toutes les commandes dans une seule clé
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1))) // fenêtre glissante de 1 min
                .aggregate(
                        () -> BigDecimal.ZERO,  //lambda explicite, pas une method reference
                        (key, value, aggregate) -> aggregate.add(eventSerializerPort.deserialize(value, OrderCreatedEvent.class).totalAmount()),
                        Materialized.<String, BigDecimal, WindowStore<Bytes, byte[]>>as("revenue-per-minute-store")
                                .withValueSerde(new BigDecimalSerde())
                );

        revenuePerMinute.toStream()
                .map((windowedKey, revenue) -> KeyValue.pair(
                        windowedKey.window().startTime().toString(), revenue.toString()))
                .to(EventTopic.REVENUE_PER_MINUTE, Produced.with(Serdes.String(), Serdes.String()));

        return orders;
    }


}
