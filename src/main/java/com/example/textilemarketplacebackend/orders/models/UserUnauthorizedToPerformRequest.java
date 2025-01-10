package com.example.textilemarketplacebackend.orders.models;

public class UserUnauthorizedToPerformRequest extends RuntimeException {
    public UserUnauthorizedToPerformRequest(String message) { super(message); }
}
