package com.example.textilemarketplacebackend.products.services;

import com.example.textilemarketplacebackend.auth.models.user.User;
import com.example.textilemarketplacebackend.products.models.*;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricComposition;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricSafetyRequirements;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricTechnology;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricType;
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

    private final ProductRepository productRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    public List<ProductDTO> getOffers() {
        return productRepository.findAll()
                .stream()
                .map(listing -> modelMapper.map(listing, ProductDTO.class))
                .collect(Collectors.toList());
    }

    public ProductDTO getOfferById(Long id) {
        ProductListing productListing = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No offer found with this Id"));
        return modelMapper.map(productListing, ProductDTO.class); // Zwracamy DTO zamiast encji
    }

    public void postOffer(String authHeader, ProductDTO productDTO) {
        User user = userService.extractUserFromToken(authHeader);
        List<MailRequest> mailRequestList = new ArrayList<>();

        ProductListing productListing = ProductListing.builder()
                .productName(productDTO.getProductName())
                .shortDescription(productDTO.getShortDescription())
                .longDescription(productDTO.getLongDescription())
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity())
                .images(productDTO.getImages())
                .fabricType(productDTO.getFabricType())
                .composition(productDTO.getComposition())
                .technologies(productDTO.getTechnologies())
                .safetyRequirements(productDTO.getSafetyRequirements())
                .colour(productDTO.getColour())
                .width(productDTO.getWidth())
                .user(user)
                .build();

        MailRequest buyerMailRequest = MailRequest.builder()
                .body(String.format("Your product with the name %s has been successfully posted. You can view your listings by clicking the button below.", productDTO.getProductName()))
                .type(MailRequestType.NOTIFICATION)
                .recipients(new String[]{user.getEmail()})
                .build();
        mailRequestList.add(buyerMailRequest);

        productRepository.save(productListing);
        emailService.sendEmail(mailRequestList);
    }

    public void editOffer(String authHeader, Long id, ProductDTO productDTO) {
        User user = userService.extractUserFromToken(authHeader);

        ProductListing existingProductListing = productRepository.findById(id)
                .filter(offer -> offer.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NoSuchElementException("Offer not found or user is not authorized"));

        existingProductListing.setProductName(productDTO.getProductName());
        existingProductListing.setShortDescription(productDTO.getShortDescription());
        existingProductListing.setLongDescription(productDTO.getLongDescription());
        existingProductListing.setPrice(productDTO.getPrice());
        existingProductListing.setQuantity(productDTO.getQuantity());
        existingProductListing.setImages(productDTO.getImages());
        existingProductListing.setFabricType(productDTO.getFabricType());
        existingProductListing.setComposition(productDTO.getComposition());
        existingProductListing.setTechnologies(productDTO.getTechnologies());
        existingProductListing.setSafetyRequirements(productDTO.getSafetyRequirements());
        existingProductListing.setColour(productDTO.getColour());
        existingProductListing.setWidth(productDTO.getWidth());

        productRepository.save(existingProductListing);
    }

    public void deleteOffer(String authHeader, Long id) {
        User user = userService.extractUserFromToken(authHeader);
        List<MailRequest> mailRequestList = new ArrayList<>();

        ProductListing existingProductListing = productRepository.findById(id)
                .filter(offer -> offer.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));

        MailRequest buyerMailRequest = MailRequest.builder()
                .body(String.format("Your product with the name %s has been successfully deleted.", existingProductListing.getProductName()))
                .type(MailRequestType.NOTIFICATION)
                .recipients(new String[]{user.getEmail()})
                .build();
        mailRequestList.add(buyerMailRequest);

        productRepository.delete(existingProductListing);
        emailService.sendEmail(mailRequestList);
    }

    public ProductEnumDTO getListingEnums() {
        return ProductEnumDTO.builder()
                .fabricTypes(FabricType.values())
                .compositions(FabricComposition.values())
                .technologies(FabricTechnology.values())
                .safetyRequirements(FabricSafetyRequirements.values())
                .build();
    }
}
