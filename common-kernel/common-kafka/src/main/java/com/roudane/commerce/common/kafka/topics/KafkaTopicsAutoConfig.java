package com.roudane.commerce.common.kafka.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class KafkaTopicsAutoConfig {


    @ConditionalOnProperty(
            name = "app.kafka.auto-create-topics",
            havingValue = "true"
    )
    @Bean
    public NewTopics applicationTopics(KafkaTopicsProperties properties) {
        List<NewTopic> topics = new ArrayList<>();

        for (KafkaTopicsProperties.TopicDefinition def : properties.getTopics()) {
            topics.add(TopicBuilder.name(def.getName())
                    .partitions(def.getPartitions())
                    .replicas(def.getReplicas())
                    .build());

            if (properties.isAutoCreateDlt()) {
                topics.add(TopicBuilder.name(def.getName() + ".DLT")
                        .partitions(def.getPartitions())
                        .replicas(def.getReplicas())
                        .build());
            }
        }

        return new NewTopics(
                topics.toArray(NewTopic[]::new));
    }
}
