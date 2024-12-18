package com.example.textilemarketplacebackend.listings.services;

import com.example.textilemarketplacebackend.auth.models.user.User;
import com.example.textilemarketplacebackend.mail.models.MailRequest;
import com.example.textilemarketplacebackend.mail.models.MailRequestType;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import com.example.textilemarketplacebackend.listings.models.ProductListing;
import com.example.textilemarketplacebackend.global.services.UserService;
import com.example.textilemarketplacebackend.listings.models.ListingDTO;
import com.example.textilemarketplacebackend.listings.models.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserService userService;
    private final EmailService emailService;

    public List<ListingDTO> getOffers() {
        return listingRepository.findAll()
                .stream()
                .map(ListingDTO::new) // Mapowanie encji Offer na DTO
                .collect(Collectors.toList());
    }

    public ListingDTO getOfferById(Long id) {
        ProductListing productListing = listingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No offer found with this Id"));
        return new ListingDTO(productListing); // Zwracamy DTO zamiast encji
    }

    public void postOffer(String authHeader, ListingDTO listingDTO) {
        User user = userService.extractUserFromToken(authHeader);
        List<MailRequest> mailRequestList = new ArrayList<>();

        ProductListing productListing = ProductListing.builder()
                .productName(listingDTO.getProductName())
                .shortDescription(listingDTO.getShortDescription())
                .longDescription(listingDTO.getLongDescription())
                .price(listingDTO.getPrice())
                .quantity(listingDTO.getQuantity())
                .user(user)
                .build();

        MailRequest buyerMailRequest = MailRequest.builder()
                .body(String.format("Your product with the name %s has been successfully posted. You can view your listings by clicking the button below.", listingDTO.getProductName()))
                .type(MailRequestType.NOTIFICATION)
                .recipients(new String[]{user.getEmail()})
                .build();
        mailRequestList.add(buyerMailRequest);

        listingRepository.save(productListing);
        emailService.sendEmail(mailRequestList);
    }

    public void editOffer(String authHeader, Long id, ListingDTO listingDTO) {

        User user = userService.extractUserFromToken(authHeader);

        // Download existing offer from DB if the user id matches
        ProductListing existingProductListing = listingRepository.findById(id)
                .filter(offer -> offer.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NoSuchElementException("Offer not found or user is not authorized"));

        // Update using DTO
        existingProductListing.setProductName(listingDTO.getProductName());
        existingProductListing.setShortDescription(listingDTO.getShortDescription());
        existingProductListing.setLongDescription(listingDTO.getLongDescription());
        existingProductListing.setPrice(listingDTO.getPrice());
        existingProductListing.setQuantity(listingDTO.getQuantity());

        listingRepository.save(existingProductListing);
    }

    public void deleteOffer(String authHeader, Long id) {
        User user = userService.extractUserFromToken(authHeader);
        List<MailRequest> mailRequestList = new ArrayList<>();

        ProductListing existingProductListing = listingRepository.findById(id)
                .filter(offer -> offer.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));

        MailRequest buyerMailRequest = MailRequest.builder()
                .body(String.format("Your product with the name %s has been successfully deleted.", existingProductListing.getProductName()))
                .type(MailRequestType.NOTIFICATION)
                .recipients(new String[]{user.getEmail()})
                .build();
        mailRequestList.add(buyerMailRequest);

        listingRepository.delete(existingProductListing);
        emailService.sendEmail(mailRequestList);
    }

}
