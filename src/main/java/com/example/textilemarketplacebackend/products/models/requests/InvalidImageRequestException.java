package com.example.textilemarketplacebackend.products.models.requests;

public class InvalidImageRequestException extends RuntimeException {
    public InvalidImageRequestException(String message) {
        super(message);
    }
}
