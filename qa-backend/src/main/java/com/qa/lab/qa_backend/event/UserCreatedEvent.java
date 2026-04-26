package com.qa.lab.qa_backend.event;

public record UserCreatedEvent(
        Long id,
        String email
) {
}