package com.example.textilemarketplacebackend.listings.controllers;

import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;

import com.example.textilemarketplacebackend.listings.models.ListingDTO;
import com.example.textilemarketplacebackend.listings.services.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ResponseHandlerService responseHandlerService;
    private final ListingService listingService;


    //Add new offer (Now using DTO)
    @PostMapping("/add")
    public ResponseEntity<Object> postOffer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @Valid @RequestBody ListingDTO listingDTO) {
        try {
            listingService.postOffer(authHeader, listingDTO);
            return responseHandlerService.generateResponse(String.format("Offer with the name %s has been posted successfully.", listingDTO.getProductName()), HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while posting your advert", HttpStatus.MULTI_STATUS, e);
        }
    }

    //Fetch offers by designated ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOfferById(@PathVariable Long id) {
        try {
            return responseHandlerService.generateResponse(String.format("Offer with id %d has been fetched", id), HttpStatus.OK, listingService.getOfferById(id));
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Fetching offer failed", HttpStatus.MULTI_STATUS, e);
        }
    }

    //Retrieve all offers from DB
    @GetMapping("/get_all")
    public ResponseEntity<Object> getOffers() {
        try {
            return responseHandlerService.generateResponse("Offers have been fetched", HttpStatus.OK, listingService.getOffers());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Fetching offers failed", HttpStatus.MULTI_STATUS, e.getMessage());
        }
    }

    // Edits exising offer
    @PatchMapping("/{id}/edit")
    public ResponseEntity<Object> editOffer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable("id") Long id,
            @Valid @RequestBody ListingDTO editedListingDTO) {
        try {
            // we need to check if the offer actually belongs to the user
            listingService.editOffer(authHeader, id, editedListingDTO);
            return responseHandlerService.generateResponse("Offer edited successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while editing your advert", HttpStatus.MULTI_STATUS, e.getMessage());
        }
    }


    // Deletes offer (Now using DTO)
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> deleteOffer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable("id") Long id) {
        try {
            listingService.deleteOffer(authHeader, id);
            return responseHandlerService.generateResponse("Offer deleted successfully", HttpStatus.NO_CONTENT,null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while deleting your advert", HttpStatus.MULTI_STATUS,e.getMessage());
        }
    }

    @GetMapping("/get-listing-enums")
    public ResponseEntity<Object> getListingEnums() {
        try {
            return responseHandlerService.generateResponse("Listing enums fetched", HttpStatus.OK, listingService.getListingEnums());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while fetching listing enums", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
