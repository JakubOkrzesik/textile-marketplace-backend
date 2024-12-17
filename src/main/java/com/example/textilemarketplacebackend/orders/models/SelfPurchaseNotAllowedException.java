package com.example.textilemarketplacebackend.orders.models;

public class SelfPurchaseNotAllowedException extends RuntimeException{
    public SelfPurchaseNotAllowedException(String message) {
        super(message);
    }
}
