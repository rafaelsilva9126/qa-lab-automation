package com.qa.lab.qa_backend.controller;

import com.qa.lab.qa_backend.dto.CreateProductRequest;
import com.qa.lab.qa_backend.dto.ProductResponse;
import com.qa.lab.qa_backend.service.ProductService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return service.createProduct(request);
    }
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return service.getAllProducts();
    }

@GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return service.getProductById(id);
    }
}