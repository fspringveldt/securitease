package com.example.store.service;

import com.example.store.config.KafkaTopic;
import com.example.store.dto.OrderDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    public static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = KafkaTopic.ORDERS, groupId = "my-spring-group")
    public void consumeOrder(OrderDTO order) {
        logger.info("Received message from Docker Kafka: {}", order);
    }
}
