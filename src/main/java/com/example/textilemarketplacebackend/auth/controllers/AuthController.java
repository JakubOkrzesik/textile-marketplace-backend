package com.example.textilemarketplacebackend.auth.controllers;

import com.example.textilemarketplacebackend.auth.models.requests.AuthenticationRequest;
import com.example.textilemarketplacebackend.auth.models.requests.ForgetPasswordRequest;
import com.example.textilemarketplacebackend.auth.models.requests.PasswordResetRequest;
import com.example.textilemarketplacebackend.auth.models.requests.RegisterRequest;
import com.example.textilemarketplacebackend.auth.models.user.NipAlreadyExistsException;
import com.example.textilemarketplacebackend.auth.models.user.UserAlreadyExistsException;
import com.example.textilemarketplacebackend.auth.services.AuthService;
import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.mail.models.InternalMailServiceErrorException;
import com.example.textilemarketplacebackend.mail.models.InvalidMailRequestException;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ResponseHandlerService responseHandler;
    private final AuthService authService;


    // endpoint registers user and sends account activation email
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return responseHandler.generateResponse("Your account has been created. Please activate it via an email that was sent to your inbox.", HttpStatus.OK, null);
        } catch (ConstraintViolationException e) {
            return responseHandler.generateResponse("Invalid request. Please check your request params.", HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UserAlreadyExistsException | NipAlreadyExistsException e) {
            return responseHandler.generateResponse("Registration unsuccessful. User with this email already exists. Please log in.", HttpStatus.CONFLICT, e.getMessage());
        } catch (InternalMailServiceErrorException e) {
            return responseHandler.generateResponse("Failure while sending the activation email.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (InvalidMailRequestException e) {
            return responseHandler.generateResponse("Invalid request sent to the email service.", HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return responseHandler.generateResponse("Registration unsuccessful. Internal error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // endpoint checks jwt activation token and activates account if validation is successful
    @PostMapping("/activate_account")
    public ResponseEntity<Object> activateAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            authService.handleSuccessfulAccountActivation(authHeader);
            return responseHandler.generateResponse("Your account has been authenticated. Please log in.", HttpStatus.ACCEPTED, null);
        } catch (UsernameNotFoundException e) {
            return responseHandler.generateResponse("User with the provided username does not exist", HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return responseHandler.generateResponse("An internal server error has occurred", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // endpoint sends jwt token when login is successful
    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authService.authenticate(request));
        } catch (Exception e) {
            return responseHandler.generateResponse("Authentication unsuccessful", HttpStatus.OK, e);
        }
    }

    // endpoint sends password reset token email if the credentials are correct
    @PostMapping("/send_reset_password_email")
    public ResponseEntity<Object> sendResetPasswordEmail(@RequestBody ForgetPasswordRequest request) {
        try {
            return responseHandler.generateResponse("An email with a password reset link should arrive in your inbox shortly", HttpStatus.ACCEPTED, authService.sendResetPasswordEmail(request));
        } catch (UsernameNotFoundException e) {
            return responseHandler.generateResponse("User with this email was not found. Try typing your email again", HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InternalMailServiceErrorException e) {
            return responseHandler.generateResponse("An mail service error occurred while processing your request", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return responseHandler.generateResponse("An error occurred while processing your request", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/validate-password-token")
    public ResponseEntity<Object> validatePasswordToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            authService.validatePasswordResetToken(authHeader);
            return responseHandler.generateResponse("Password has been validated.", HttpStatus.OK, null);
        } catch (IllegalArgumentException e) {
            return responseHandler.generateResponse("Invalid token inside request.", HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return responseHandler.generateResponse("An error occurred while processing your request", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // endpoint validates user password reset
    @PostMapping("/reset_password")
    public ResponseEntity<Object> resetPassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestBody PasswordResetRequest request) {
        try {
            authService.handleSuccessfulPasswordChange(authHeader, request);
            return responseHandler.generateResponse("Password changed successfully. You may now log in.", HttpStatus.OK, null);
        } catch (UsernameNotFoundException e) {
            return responseHandler.generateResponse("User with the provided username does not exist", HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return responseHandler.generateResponse("Password reset token invalid", HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return responseHandler.generateResponse("An internal server error has occurred", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
