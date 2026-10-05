package com.roudane.commerce.common.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerErrorHandlingConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerErrorHandlingConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.schema-registry.url}")
    private String schemaRegistryUrl;

    // ---------- Consumer Factory avec ErrorHandlingDeserializer ----------
    // Protège contre les messages malformés (JSON corrompu) qui, sans ça,
    // planteraient le consumer AVANT même d'atteindre ton code métier.

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // Enrobe le vrai désérialiseur pour capturer les erreurs de désérialisation
        // proprement, au lieu de faire planter tout le conteneur
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);

        // 3. Configuration spécifique au Schema Registry
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        // Indispensable : force la désérialisation vers tes classes Avro générées (SpecificRecord),
        // sinon tu obtiens des GenericRecord génériques, moins pratiques à manipuler
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        // Commit manuel : on ne veut PAS que Kafka avance l'offset avant confirmation explicite
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Repart du début du topic si aucun offset connu (premier démarrage)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Limite le nombre de messages traités par poll, pour un contrôle plus fin
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    // ---------- Error Handler : retry avec backoff exponentiel + DLT ----------

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaOperations<String, Object> kafkaOperations) {
        // Backoff exponentiel : 1s, 2s, 4s, 8s, 16s (au lieu d'un délai fixe)
        // Évite de marteler un système en panne avec la même cadence
        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxElapsedTime(30_000L); // abandonne après 30s total de tentatives

        // Publie le message en échec définitif sur un topic "<topic-original>.DLT"
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaOperations,
                (record, ex) -> {
                    log.error("Message définitivement en échec, envoi vers DLT : topic={} partition={} offset={} key={} raison={}",
                            record.topic(), record.partition(), record.offset(), record.key(), ex.getMessage());
                    return new TopicPartition(record.topic() + ".DLT", record.partition());
                }
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // Exceptions à NE JAMAIS retenter (erreurs définitives, retenter ne sert à rien)
        errorHandler.addNotRetryableExceptions(
                JsonProcessingException.class,
                IllegalArgumentException.class
        );

        // Log à chaque tentative, utile pour observer le comportement en cours de retry
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn("Tentative {} échouée pour le message key={} sur {} : {}",
                        deliveryAttempt, record.key(), record.topic(), ex.getMessage())
        );

        return errorHandler;
    }

    // ---------- Listener Container Factory ----------

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DefaultErrorHandler kafkaErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);

        // Ack manuel après traitement RÉUSSI uniquement — évite la perte silencieuse
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        // Nombre de threads consommateurs en parallèle (concurrency = partitions à répartir)
        factory.setConcurrency(1);

        return factory;
    }
}
