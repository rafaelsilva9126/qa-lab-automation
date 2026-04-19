package com.qa.lab.qa_backend.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private Double price;
    private Long userId;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name, Double price, Long userId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }
 
    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getUserId() {
        return userId;
    }
 
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}