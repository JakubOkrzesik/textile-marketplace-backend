package com.example.textilemarketplacebackend.offers.services;

import com.example.textilemarketplacebackend.db.models.LocalUser;
import com.example.textilemarketplacebackend.db.models.Offer;
import com.example.textilemarketplacebackend.global.services.UserService;
import com.example.textilemarketplacebackend.offers.models.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserService userService;

    public List<Offer> getOffers() {
        return offerRepository.getOffers();
    }

    public Offer getOfferById(Long id) {
        return offerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No offer found with this Id"));
    }

    public void postOffer(String authHeader, Offer offer) {
        LocalUser user = userService.extractUserFromToken(authHeader);
        offer.setUser(user);
        offerRepository.save(offer);
    }

    public void editOffer(String authHeader, Long id, Offer editedOffer) {
        Offer offer = getOfferById(id);
        offer.setProductName(editedOffer.getProductName());
        offer.setShortDescription(editedOffer.getShortDescription());
        offer.setLongDescription(editedOffer.getLongDescription());
        offer.setPrice(editedOffer.getPrice());
        offer.setQuantity(editedOffer.getQuantity());
        offerRepository.save(offer);
    }

    public void deleteOffer(Long id) {
        Offer offer = getOfferById(id);
        offerRepository.delete(offer);
    }
}
