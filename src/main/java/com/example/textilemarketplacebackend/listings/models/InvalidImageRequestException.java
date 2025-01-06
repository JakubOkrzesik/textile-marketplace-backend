package com.example.textilemarketplacebackend.listings.models;

public class InvalidImageRequestException extends RuntimeException {
    public InvalidImageRequestException(String message) {
        super(message);
    }
}
