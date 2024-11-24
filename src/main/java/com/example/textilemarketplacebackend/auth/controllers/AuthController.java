package com.example.textilemarketplacebackend.auth.controllers;

import com.example.textilemarketplacebackend.auth.models.requests.AuthenticationRequest;
import com.example.textilemarketplacebackend.auth.models.requests.RegisterRequest;
import com.example.textilemarketplacebackend.auth.models.user.UserAlreadyExistsException;
import com.example.textilemarketplacebackend.auth.services.AuthService;
import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ResponseHandlerService responseHandler;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        try {
            return responseHandler.generateResponse(authService.register(request), HttpStatus.OK, null);
        } catch (Exception | UserAlreadyExistsException e) {
            return responseHandler.generateResponse("Registration unsuccessful", HttpStatus.OK, e);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authService.authenticate(request));
        } catch (Exception e) {
            return responseHandler.generateResponse("Authentication unsuccessful", HttpStatus.OK, e);
        }
    }
}
