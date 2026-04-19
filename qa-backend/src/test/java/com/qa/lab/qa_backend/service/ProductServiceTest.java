package com.qa.lab.qa_backend.service;

import com.qa.lab.qa_backend.dto.CreateProductRequest;
import com.qa.lab.qa_backend.dto.ProductResponse;
import com.qa.lab.qa_backend.model.Product;
import com.qa.lab.qa_backend.model.User;
import com.qa.lab.qa_backend.repository.ProductRepository;
import com.qa.lab.qa_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService service;

    @Test
    void shouldCreateProductSuccessfully() {

        CreateProductRequest request =
                new CreateProductRequest("Keyboard", 100.0, 1L);

        User user = new User();
        user.setId(1L);

        Product savedProduct = new Product();
        savedProduct.setId(10L);
        savedProduct.setName("Keyboard");
        savedProduct.setPrice(100.0);
        savedProduct.setUser(user);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response = service.createProduct(request);

        assertEquals(10L, response.getId());
        assertEquals("Keyboard", response.getName());
        assertEquals(100.0, response.getPrice());
        assertEquals(1L, response.getUserId());
    }
    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        CreateProductRequest request =
                new CreateProductRequest("Keyboard", 100.0, 999L);

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.createProduct(request)
        );

        assertEquals("User not found", exception.getMessage());
    }
}