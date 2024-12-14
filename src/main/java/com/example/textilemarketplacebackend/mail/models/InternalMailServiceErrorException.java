package com.example.textilemarketplacebackend.mail.models;

public class InternalMailServiceErrorException extends RuntimeException{
    public InternalMailServiceErrorException(String message) {
        super(message);
    }
}
