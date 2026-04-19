package com.qa.lab.qa_backend.service;

import com.qa.lab.qa_backend.dto.CreateUserRequest;
import com.qa.lab.qa_backend.dto.UserResponse;
import com.qa.lab.qa_backend.model.User;
import com.qa.lab.qa_backend.repository.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserResponse createUser(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = repository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getEmail());
    }
    public List<UserResponse> getAllUsers() {
    return repository.findAll()
            .stream()
            .map(user -> new UserResponse(user.getId(), user.getEmail()))
            .toList();
}
}