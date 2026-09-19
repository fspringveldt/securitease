package com.example.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        // Match the ".DLT" topics created in KafkaTopicConfig; let Kafka pick the partition
        // since DLT topics have fewer partitions than their source topics.
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                template,
                (record, ex) -> new org.apache.kafka.common.TopicPartition(record.topic() + KafkaTopic.DLT_SUFFIX, -1));

        // Retry twice, waiting 2 seconds between attempts
        FixedBackOff backoff = new FixedBackOff(2000L, 2);

        return new DefaultErrorHandler(recoverer, backoff);
    }
}
