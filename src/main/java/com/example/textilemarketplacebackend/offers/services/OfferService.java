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

    public void postOffer(String authHeader, Offer offer) {
        LocalUser user = userService.extractUserFromToken(authHeader);
        offer.setUser(user);
        offerRepository.save(offer);
    }

    public void editOffer(String authHeader, Long id, OfferDTO editedOfferDTO) {
        // Download from DB
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Offer not found"));
        offer.setProductName(editedOfferDTO.getProductName());
        offer.setShortDescription(editedOfferDTO.getShortDescription());
        offer.setLongDescription(editedOfferDTO.getLongDescription());
        offer.setPrice(editedOfferDTO.getPrice());
        offer.setQuantity(editedOfferDTO.getQuantity());
        offerRepository.save(offer);
    }



    public void deleteOffer(Long id) {
        OfferDTO offer = getOfferById(id);
        offerRepository.delete(offer);
    }
}
