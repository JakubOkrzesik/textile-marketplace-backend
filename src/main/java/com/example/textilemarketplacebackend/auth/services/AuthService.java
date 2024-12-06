package com.example.textilemarketplacebackend.auth.services;

import com.example.textilemarketplacebackend.auth.models.requests.AuthenticationRequest;
import com.example.textilemarketplacebackend.auth.models.requests.ForgetPasswordRequest;
import com.example.textilemarketplacebackend.auth.models.requests.TokenResponse;
import com.example.textilemarketplacebackend.auth.models.requests.RegisterRequest;
import com.example.textilemarketplacebackend.auth.models.user.Role;
import com.example.textilemarketplacebackend.auth.models.user.UserAlreadyExistsException;
import com.example.textilemarketplacebackend.auth.models.user.UserRepository;
import com.example.textilemarketplacebackend.db.models.LocalUser;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public String register(RegisterRequest request) throws UserAlreadyExistsException {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("This email address is already tied to a user");
        }
        LocalUser user = LocalUser.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nip(request.getNip())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return "Your account has been created";
    }

    public TokenResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        // db query two times which is not ideal

        LocalUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateAuthToken(user);
        return TokenResponse.builder().token(jwtToken).build();
    }

    public TokenResponse resetPassword(ForgetPasswordRequest request) {
        LocalUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String resetToken = jwtService.generatePasswordResetToken(user);
        return TokenResponse.builder().token(resetToken).build();
    }
}
