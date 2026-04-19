package com.qa.lab.qa_backend.repository;

import com.qa.lab.qa_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}