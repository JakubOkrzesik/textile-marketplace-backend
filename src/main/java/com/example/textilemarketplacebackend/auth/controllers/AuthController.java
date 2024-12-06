package com.example.textilemarketplacebackend.auth.controllers;

import com.example.textilemarketplacebackend.auth.models.requests.AuthenticationRequest;
import com.example.textilemarketplacebackend.auth.models.requests.ForgetPasswordRequest;
import com.example.textilemarketplacebackend.auth.models.requests.RegisterRequest;
import com.example.textilemarketplacebackend.auth.models.user.UserAlreadyExistsException;
import com.example.textilemarketplacebackend.auth.services.AuthService;
import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        } catch (UserAlreadyExistsException e) {
            return responseHandler.generateResponse("Registration unsuccessful. User with this email already exists. Please log in.", HttpStatus.CONFLICT, e);
        } catch (Exception e) {
            return responseHandler.generateResponse("Registration unsuccessful. Internal error", HttpStatus.INTERNAL_SERVER_ERROR, e);
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

    @PostMapping("/reset_password")
    public ResponseEntity<Object> resetPasword(@RequestBody ForgetPasswordRequest request) {
        try {
            return responseHandler.generateResponse("An email with a password reset link should arrive in your inbox shortly", HttpStatus.ACCEPTED, authService.resetPassword(request));
        } catch (UsernameNotFoundException e) {
            return responseHandler.generateResponse("User with this email was not found. Try typing your email again", HttpStatus.NOT_FOUND, null);
        } catch ()
    }
}
