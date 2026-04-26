package com.qa.lab.qa_backend.service;

import com.qa.lab.qa_backend.dto.CreateUserRequest;
import com.qa.lab.qa_backend.dto.UserResponse;
import com.qa.lab.qa_backend.event.UserCreatedEvent;
import com.qa.lab.qa_backend.model.User;
import com.qa.lab.qa_backend.producer.UserEventProducer;
import com.qa.lab.qa_backend.repository.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserEventProducer producer;

    public UserService(UserRepository repository, UserEventProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

  public UserResponse createUser(CreateUserRequest request) {

    producer.publishUserCreated(
            new UserCreatedEvent(null, request.getEmail())
    );

    return new UserResponse(null, request.getEmail());
}

    public List<UserResponse> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail()))
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(user.getId(), user.getEmail());
    }
}