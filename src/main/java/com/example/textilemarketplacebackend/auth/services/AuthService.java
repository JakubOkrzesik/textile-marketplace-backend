package com.example.textilemarketplacebackend.auth.services;

import com.example.textilemarketplacebackend.auth.models.requests.AuthenticationRequest;
import com.example.textilemarketplacebackend.auth.models.requests.AuthenticationResponse;
import com.example.textilemarketplacebackend.auth.models.requests.RegisterRequest;
import com.example.textilemarketplacebackend.auth.models.user.Role;
import com.example.textilemarketplacebackend.auth.models.user.UserAlreadyExistsException;
import com.example.textilemarketplacebackend.auth.models.user.UserRepository;
import com.example.textilemarketplacebackend.db.models.LocalUser;
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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        LocalUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
