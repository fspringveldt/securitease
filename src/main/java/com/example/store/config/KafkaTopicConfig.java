package com.example.store.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopics addTopics() {
        // String[] topicNames = { "store-orders" };

        // return new NewTopics(
        // TopicBuilder.name("store-orders")
        // .partitions(3)
        // .replicas(1)
        // .build(),
        // TopicBuilder.name("store-orders.DLT")
        // .partitions(1)
        // .replicas(1)
        // .build());

        List<NewTopic> topics = new ArrayList<>();
        for (String topicName : KafkaTopic.ALL_TOPICS) {
            if (topicName == null || topicName.isBlank()) {
                continue;
            }
            topics.add(TopicBuilder.name(topicName).partitions(3).replicas(1).build());
            topics.add(TopicBuilder.name(topicName + KafkaTopic.DLT_SUFFIX)
                    .partitions(1)
                    .replicas(1)
                    .build());
        }

        return new NewTopics(topics.toArray(new NewTopic[0]));
    }
}
