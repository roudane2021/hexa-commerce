package com.roudane.commerce.analytic.config;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaStreamsConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analytics.streams.replication-factor}")
    private int replicationFactor;

    @Value("${analytics.streams.state-dir}")
    private String stateDir;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "analytics-service-streams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serdes par défaut : String partout, cohérent avec le reste de ton pipeline Outbox
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Nombre de threads de traitement en parallèle (scalabilité horizontale de la topology)
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);

        // Garantit qu'un message n'est traité EXACTEMENT UNE FOIS à travers toute la topology
        // (lecture + traitement + écriture atomiques) — le plus haut niveau de fiabilité Kafka Streams
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);

        // Commit plus fréquent en dev pour voir les résultats vite (défaut: 30000ms en EOS)
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);

        // Où sont stockés les state stores locaux (RocksDB) — persistant entre redémarrages
        props.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);

        // Ne fait JAMAIS planter tout le stream sur une erreur de désérialisation isolée —
        // log l'erreur et continue avec le message suivant (équivalent DLT pour Streams)
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                StreamsDeserializationErrorHandler.class);

        // Réplication du changelog topic (state store backup) — 3 pour cohérence avec ton cluster
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, replicationFactor);

        // Timeout de production interne au streams (résilience réseau)
        // Augmente delivery.timeout.ms pour couvrir request.timeout.ms (30s) + linger.ms
        props.put(StreamsConfig.PRODUCER_PREFIX + "delivery.timeout.ms", 120000);

        return new KafkaStreamsConfiguration(props);
    }
}