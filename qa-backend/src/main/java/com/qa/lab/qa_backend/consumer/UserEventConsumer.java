package com.qa.lab.qa_backend.consumer;

import com.qa.lab.qa_backend.event.UserCreatedEvent;
import com.qa.lab.qa_backend.model.User;
import com.qa.lab.qa_backend.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private final UserRepository repository;

    public UserEventConsumer(UserRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "${app.kafka.topics.user-created}", groupId = "qa-group")
    public void consume(UserCreatedEvent event) throws InterruptedException {
        Thread.sleep(3000);
        User user = new User();
        user.setEmail(event.email());
        user.setPassword("default"); // simulação

        repository.save(user);

        System.out.println("🔥 Evento consumido e salvo no banco: " + event.email());
    }
}