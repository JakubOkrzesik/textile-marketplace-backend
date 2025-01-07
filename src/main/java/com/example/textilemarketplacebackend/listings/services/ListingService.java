package com.example.textilemarketplacebackend.listings.services;

import com.example.textilemarketplacebackend.auth.models.user.User;
import com.example.textilemarketplacebackend.listings.models.*;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricComposition;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricSafetyRequirements;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricTechnology;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricType;
import com.example.textilemarketplacebackend.mail.models.MailRequest;
import com.example.textilemarketplacebackend.mail.models.MailRequestType;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import com.example.textilemarketplacebackend.global.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public List<ListingDTO> getOffers() {
        return listingRepository.findAll()
                .stream()
                .map(listing -> modelMapper.map(listing, ListingDTO.class))
                .collect(Collectors.toList());
    }

    public ListingDTO getOfferById(Long id) {
        ProductListing productListing = listingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No offer found with this Id"));
        return modelMapper.map(productListing, ListingDTO.class); // Zwracamy DTO zamiast encji
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
                .images(listingDTO.getImages())
                .fabricType(listingDTO.getFabricType())
                .composition(listingDTO.getComposition())
                .technologies(listingDTO.getTechnologies())
                .safetyRequirements(listingDTO.getSafetyRequirements())
                .colour(listingDTO.getColour())
                .width(listingDTO.getWidth())
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

        ProductListing existingProductListing = listingRepository.findById(id)
                .filter(offer -> offer.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NoSuchElementException("Offer not found or user is not authorized"));

        existingProductListing.setProductName(listingDTO.getProductName());
        existingProductListing.setShortDescription(listingDTO.getShortDescription());
        existingProductListing.setLongDescription(listingDTO.getLongDescription());
        existingProductListing.setPrice(listingDTO.getPrice());
        existingProductListing.setQuantity(listingDTO.getQuantity());
        existingProductListing.setImages(listingDTO.getImages());
        existingProductListing.setFabricType(listingDTO.getFabricType());
        existingProductListing.setComposition(listingDTO.getComposition());
        existingProductListing.setTechnologies(listingDTO.getTechnologies());
        existingProductListing.setSafetyRequirements(listingDTO.getSafetyRequirements());
        existingProductListing.setColour(listingDTO.getColour());
        existingProductListing.setWidth(listingDTO.getWidth());

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

    public ListingEnumDTO getListingEnums() {
        return ListingEnumDTO.builder()
                .fabricTypes(FabricType.values())
                .compositions(FabricComposition.values())
                .technologies(FabricTechnology.values())
                .safetyRequirements(FabricSafetyRequirements.values())
                .build();
    }
}
