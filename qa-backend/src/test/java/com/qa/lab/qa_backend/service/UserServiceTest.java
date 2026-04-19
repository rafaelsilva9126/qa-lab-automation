package com.qa.lab.qa_backend.service;

import com.qa.lab.qa_backend.dto.CreateUserRequest;
import com.qa.lab.qa_backend.dto.UserResponse;
import com.qa.lab.qa_backend.model.User;
import com.qa.lab.qa_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void shouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("test@mail.com", "123456");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@mail.com");    
        savedUser.setPassword("123456");

        when(repository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = service.createUser(request);

        assertEquals(1L, response.getId());
        assertEquals("test@mail.com", response.getEmail());

        verify(repository).save(any(User.class));
    }
}