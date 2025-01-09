package com.example.textilemarketplacebackend.products.models;

public class InvalidImageRequestException extends RuntimeException {
    public InvalidImageRequestException(String message) {
        super(message);
    }
}
