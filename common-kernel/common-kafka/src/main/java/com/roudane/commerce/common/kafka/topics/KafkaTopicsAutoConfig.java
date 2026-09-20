package com.roudane.commerce.common.kafka.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class KafkaTopicsAutoConfig {


    @Bean
    public List<NewTopic> applicationTopics(KafkaTopicsProperties properties) {
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

        return topics;
    }
}
