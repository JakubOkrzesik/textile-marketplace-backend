package com.example.textilemarketplacebackend.offers.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListingRepository extends JpaRepository<ProductListing, Integer> {

    Optional<ProductListing> findById(Long id);
}