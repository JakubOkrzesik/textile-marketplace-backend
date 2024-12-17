package com.example.textilemarketplacebackend.auth.models.user;

public class NipAlreadyExistsException extends RuntimeException{
    public NipAlreadyExistsException(String message) {
        super(message);
    }
}
