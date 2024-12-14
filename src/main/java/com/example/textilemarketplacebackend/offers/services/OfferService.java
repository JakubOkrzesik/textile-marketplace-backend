package com.example.textilemarketplacebackend.offers.services;

import com.example.textilemarketplacebackend.db.models.LocalUser;
import com.example.textilemarketplacebackend.db.models.Offer;
import com.example.textilemarketplacebackend.global.services.UserService;
import com.example.textilemarketplacebackend.offers.models.OfferDTO;
import com.example.textilemarketplacebackend.offers.models.OfferRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserService userService;

    public List<OfferDTO> getOffers() {
        return offerRepository.findAll()
                .stream()
                .map(OfferDTO::new) // Mapowanie encji Offer na DTO
                .collect(Collectors.toList());
    }

    public OfferDTO getOfferById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No offer found with this Id"));
        return new OfferDTO(offer); // Zwracamy DTO zamiast encji
    }

    public void postOffer(String authHeader, OfferDTO offerDTO) {
        Offer offer = new Offer();
        offer.setProductName(offerDTO.getProductName());
        offer.setShortDescription(offerDTO.getShortDescription());
        offer.setLongDescription(offerDTO.getLongDescription());
        offer.setPrice(offerDTO.getPrice());
        offer.setQuantity(offerDTO.getQuantity());
        LocalUser user = userService.extractUserFromToken(authHeader);
        offer.setUser(user);
        offerRepository.save(offer);
    }

    public void editOffer(String authHeader, Long id, OfferDTO offerDTO) {
        // Download existing offer from DB
        Offer existingOffer = offerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));

        // Update using DTO
        existingOffer.setProductName(offerDTO.getProductName());
        existingOffer.setShortDescription(offerDTO.getShortDescription());
        existingOffer.setLongDescription(offerDTO.getLongDescription());
        existingOffer.setPrice(offerDTO.getPrice());
        existingOffer.setQuantity(offerDTO.getQuantity());
        offerRepository.save(existingOffer);
    }

    public void deleteOffer(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));

        offerRepository.delete(offer);
    }

}
