package com.example.textilemarketplacebackend.auth.models.user;

import com.example.textilemarketplacebackend.db.models.LocalUser;
import com.example.textilemarketplacebackend.db.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<LocalUser, Integer> {
    Optional<LocalUser> findByEmail(String email);
    Optional<LocalUser> findById(Long id);
}
