package com.example.textilemarketplacebackend.products.models;

import com.example.textilemarketplacebackend.auth.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductListing, Integer> {

    Optional<ProductListing> findById(Long id);

    Optional<List<ProductListing>> findAllByUser(User user);
}