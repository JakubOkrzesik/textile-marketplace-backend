package com.example.textilemarketplacebackend.auth.models.user;

public class MismatchedTokenTypeException extends RuntimeException {
    public MismatchedTokenTypeException(String message) {
        super(message);
    }
}
