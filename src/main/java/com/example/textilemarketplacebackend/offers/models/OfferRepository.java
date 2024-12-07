package com.example.textilemarketplacebackend.offers.models;

import com.example.textilemarketplacebackend.db.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Integer> {

    Optional<Offer> findById(Long id);
}