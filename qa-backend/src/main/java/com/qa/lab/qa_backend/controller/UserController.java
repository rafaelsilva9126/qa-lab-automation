package com.qa.lab.qa_backend.controller;

import com.qa.lab.qa_backend.dto.CreateUserRequest;
import com.qa.lab.qa_backend.dto.UserResponse;
import com.qa.lab.qa_backend.service.UserService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return service.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }
}