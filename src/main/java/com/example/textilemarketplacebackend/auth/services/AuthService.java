package com.example.textilemarketplacebackend.auth.services;

import com.example.textilemarketplacebackend.auth.models.requests.*;
import com.example.textilemarketplacebackend.auth.models.user.NipAlreadyExistsException;
import com.example.textilemarketplacebackend.auth.models.user.Role;
import com.example.textilemarketplacebackend.auth.models.user.UserAlreadyExistsException;
import com.example.textilemarketplacebackend.auth.models.user.User;
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

    // both these methods are meant to validate generated tokens
    public boolean authenticatePasswordResetToken(String jwt, User user){
        return jwtService.isPasswordTokenValid(jwt, user);
    }

    public boolean authenticateAccountActivationToken(String jwt, User user) {
        return jwtService.isAccountActivationTokenValid(jwt, user);
    }

    public MailResponseWrapper<List<MailResponse>> sendResetPasswordEmail(ForgetPasswordRequest request) throws InternalMailServiceErrorException {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String resetToken = jwtService.generatePasswordResetToken(user);

        MailRequest mailRequest = MailRequest.builder()
                .url(String.format("%s/password_reset?token=%s", "front_url", resetToken))
                .type(MailRequestType.PASSWORD_RESET)
                .recipients(new String[]{request.getEmail()})
                .build();

        List<MailRequest> mailRequestList = new ArrayList<>();
        mailRequestList.add(mailRequest);

        return emailService.sendEmail(mailRequestList);
    }

    public void sendAccountActivationEmail(User user) {
        MailRequest mailRequest = MailRequest.builder()
                .url(String.format("%s/account_activation?token=%s", "https://front_url.com", jwtService.generateAccountActivationToken(user)))
                .type(MailRequestType.ACCOUNT_ACTIVATION)
                .recipients(new String[]{user.getEmail()})
                .build();

        List<MailRequest> mailRequestList = new ArrayList<>();
        mailRequestList.add(mailRequest);

        emailService.sendEmail(mailRequestList);
    }

    public void handleSuccessfulPasswordChange(String authHeader, PasswordResetRequest request) {
        User user = userService.extractUserFromToken(authHeader);
        String jwt = authHeader.substring(7);

        if (!authenticatePasswordResetToken(jwt, user)) {
            throw new IllegalArgumentException("Invalid or expired password reset token");
        }

        user.setPassword(passwordEncoder.encode(request.getNew_password()));
        userService.save(user);
    }

    // idiotic code needs reevaluation
    public void handleSuccessfulAccountActivation(String authHeader) {
        User user = userService.extractUserFromToken(authHeader);
        String jwt = authHeader.substring(7);

        if (!authenticateAccountActivationToken(jwt, user)) {
            throw new IllegalArgumentException("Invalid or expired account activation token");
        };

        user.setActivated(true);
        userService.save(user);
    }
}
