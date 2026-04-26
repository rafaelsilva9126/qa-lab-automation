package com.qa.lab.qa_backend.producer;

import com.qa.lab.qa_backend.event.UserCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private final String topic;

    public UserEventProducer(
            KafkaTemplate<String, UserCreatedEvent> kafkaTemplate,
            @Value("${app.kafka.topics.user-created}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishUserCreated(UserCreatedEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.id()), event);
    }
}