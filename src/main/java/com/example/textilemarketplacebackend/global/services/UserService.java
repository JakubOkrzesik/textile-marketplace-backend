package com.example.textilemarketplacebackend.global.services;

import com.example.textilemarketplacebackend.auth.models.user.UserRepository;
import com.example.textilemarketplacebackend.auth.services.JwtService;
import com.example.textilemarketplacebackend.auth.models.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User extractUserFromToken(String authHeader) {
        String jwt = authHeader.substring(7);
        String email = jwtService.extractUsername(jwt);

        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with this email was not found"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByNip(String nip) {
        return userRepository.findByNip(nip);
    }
}
