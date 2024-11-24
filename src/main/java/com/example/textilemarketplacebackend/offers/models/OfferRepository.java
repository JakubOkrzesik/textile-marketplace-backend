package com.example.textilemarketplacebackend.offers.models;

import com.example.textilemarketplacebackend.db.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Integer> {
    Optional<List<Offer>> getOffers();

    Optional<Offer> findById(Long id);
}
