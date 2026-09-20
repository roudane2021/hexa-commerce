package com.roudane.commerce.analytic.topology;


import com.roudane.commerce.common.messaging.event.EventTopic;
import com.roudane.commerce.common.messaging.event.OrderCreatedEvent;
import com.roudane.commerce.common.messaging.event.PaymentValidatedEvent;
import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OrderPaymentJoinTopology {

    private final EventSerializerPort eventSerializerPort;

    public OrderPaymentJoinTopology(final EventSerializerPort eventSerializerPort) {
        this.eventSerializerPort = eventSerializerPort;
    }

    @Bean
    public KStream<String, String> orderPaymentDelayStream(StreamsBuilder streamsBuilder) {
        // Reclé chaque flux par orderId pour permettre la jointure
        KStream<String, String> ordersByOrderId = streamsBuilder
                .stream(EventTopic.ORDER_CREATED.getName(), Consumed.with(Serdes.String(), Serdes.String()))
                .selectKey((key, value) -> eventSerializerPort.deserialize(value, OrderCreatedEvent.class).orderId().toString());

        KStream<String, String> paymentsByOrderId = streamsBuilder
                .stream(EventTopic.PAYMENT_VALIDATED.getName(), Consumed.with(Serdes.String(), Serdes.String()))
                .selectKey((key, value) -> eventSerializerPort.deserialize(value, PaymentValidatedEvent.class).orderId().toString());

        // Jointure sur une fenêtre de 10 minutes : associe une commande à son paiement
        // s'ils partagent le même orderId ET arrivent à moins de 10 min d'écart
        KStream<String, String> joined = ordersByOrderId.join(
                paymentsByOrderId,
                (orderJson, paymentJson) -> {
                    OrderCreatedEvent order = eventSerializerPort.deserialize(orderJson, OrderCreatedEvent.class);
                    PaymentValidatedEvent payment = eventSerializerPort.deserialize(paymentJson, PaymentValidatedEvent.class);
                    long delayMs = payment.occurredAt().toEpochMilli() - order.occurredAt().toEpochMilli();
                    return "orderId=" + order.orderId() + " delaiMs=" + delayMs;
                },
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(10)),
                StreamJoined.with(Serdes.String(), Serdes.String(), Serdes.String())
        );

        joined.to(EventTopic.ORDER_PAYMENT_DELAY, Produced.with(Serdes.String(), Serdes.String()));

        return joined;
    }

}