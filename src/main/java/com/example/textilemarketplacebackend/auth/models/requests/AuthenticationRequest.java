package com.example.textilemarketplacebackend.auth.models.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthenticationRequest {
    private String email;
    private String password;
}
