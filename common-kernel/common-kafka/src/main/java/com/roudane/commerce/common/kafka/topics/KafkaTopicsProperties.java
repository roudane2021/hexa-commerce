package com.roudane.commerce.common.kafka.topics;



import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaTopicsProperties {

    private List<TopicDefinition> topics = List.of();
    private boolean autoCreateDlt = true;

    public List<TopicDefinition> getTopics() { return topics; }
    public void setTopics(List<TopicDefinition> topics) { this.topics = topics; }

    public boolean isAutoCreateDlt() { return autoCreateDlt; }
    public void setAutoCreateDlt(boolean autoCreateDlt) { this.autoCreateDlt = autoCreateDlt; }

    public static class TopicDefinition {
        private String name;
        private int partitions = 3;
        private short replicas = 1;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getPartitions() { return partitions; }
        public void setPartitions(int partitions) { this.partitions = partitions; }

        public short getReplicas() { return replicas; }
        public void setReplicas(short replicas) { this.replicas = replicas; }
    }
}
