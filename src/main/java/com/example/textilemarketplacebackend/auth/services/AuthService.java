package com.example.textilemarketplacebackend.auth.services;

import com.example.textilemarketplacebackend.auth.models.TokenType;
import com.example.textilemarketplacebackend.auth.models.requests.*;
import com.example.textilemarketplacebackend.auth.models.user.*;
import com.example.textilemarketplacebackend.global.services.EnvService;
import com.example.textilemarketplacebackend.global.services.UserService;
import com.example.textilemarketplacebackend.mail.models.*;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserService userService;
    private final EnvService envService;

    public void register(RegisterRequest request) throws UserAlreadyExistsException, InternalMailServiceErrorException {
        if (userService.findByEmail(request.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("This email address is already tied to a user");
        }

        if (userService.findByNip(request.getNip()).isPresent()) {
            throw new NipAlreadyExistsException("Nip is already tied to a user");
        }

        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nip(request.getNip())
                .role(Role.USER)
                .isActivated(false)
                .build();

        sendAccountActivationEmail(user);
        // an email is sent to the user with the account activation jwt and isActivated is set to false

        userService.save(user);
    }

    public TokenResponse authenticate(AuthenticationRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // this enables not searching the db two times
        User user = (User) auth.getPrincipal();

        String jwtToken = jwtService.generateAuthToken(user);
        return TokenResponse.builder().token(jwtToken).build();
    }

    public MailResponseWrapper<List<MailResponse>> sendResetPasswordEmail(ForgetPasswordRequest request) throws InternalMailServiceErrorException {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String resetToken = jwtService.generatePasswordResetToken(user);

        MailRequest mailRequest = MailRequest.builder()
                .url(String.format("%s/password-reset?token=%s", envService.getFRONTEND_SERVICE_URL(), resetToken))
                .type(MailRequestType.PASSWORD_RESET)
                .recipients(new String[]{request.getEmail()})
                .build();

        return emailService.sendEmail(List.of(mailRequest));
    }

    public void sendAccountActivationEmail(User user) {
        MailRequest mailRequest = MailRequest.builder()
                .url(String.format("%s/account-activation?token=%s", envService.getFRONTEND_SERVICE_URL(), jwtService.generateAccountActivationToken(user)))
                .type(MailRequestType.ACCOUNT_ACTIVATION)
                .recipients(new String[]{user.getEmail()})
                .build();

        emailService.sendEmail(List.of(mailRequest));
    }

    private User validateTokenAndGetUser(String authHeader, TokenType tokenType) {
        if (authHeader == null) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        User user = userService.extractUserFromToken(authHeader);
        String jwt = authHeader.substring(7);

        boolean isValidToken = switch (tokenType) {
            case PASSWORD_RESET -> jwtService.isPasswordTokenValid(jwt, user);
            case ACCOUNT_ACTIVATION -> jwtService.isAccountActivationTokenValid(jwt, user);
            case AUTH -> throw new MismatchedTokenTypeException("A password reset or account activation token is required");
        };

        if (!isValidToken || user.isActivated()) {
            throw new IllegalArgumentException("Invalid or expired " + tokenType.name().toLowerCase().replace("_", " ") + " token");
        }

        return user;
    }

    public void handleSuccessfulPasswordChange(String authHeader, PasswordResetRequest request) {
        User user = validateTokenAndGetUser(authHeader, TokenType.PASSWORD_RESET);

        user.setPassword(passwordEncoder.encode(request.getNew_password()));
        userService.save(user);
    }

    public void handleSuccessfulAccountActivation(String authHeader) {
        User user = validateTokenAndGetUser(authHeader, TokenType.ACCOUNT_ACTIVATION);

        user.setActivated(true);
        userService.save(user);
    }

    public void validatePasswordResetToken(String authHeader) {
        if (authHeader == null) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String jwt = authHeader.substring(7);
        User user = userService.extractUserFromToken(jwt);

        boolean isValidToken = jwtService.isPasswordTokenValid(jwt, user);

        if (!isValidToken) {
            throw new IllegalArgumentException("Password token expired or invalid");
        }
    }
}
