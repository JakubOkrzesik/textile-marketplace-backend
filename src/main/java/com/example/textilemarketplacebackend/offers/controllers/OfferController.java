package com.example.textilemarketplacebackend.offers.controllers;

import com.example.textilemarketplacebackend.db.models.Offer;
import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;

import com.example.textilemarketplacebackend.offers.models.OfferDTO;
import com.example.textilemarketplacebackend.offers.services.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class OfferController {

    private final ResponseHandlerService responseHandlerService;
    private final OfferService offerService;

    //Fetch offers by designated ID
    @GetMapping("/offers/{id}")
    public ResponseEntity<Object> getOfferById(@PathVariable Long id) {
        try {
            return responseHandlerService.generateResponse("", HttpStatus.OK, offerService.getOfferById(id));
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Fetching offer failed", HttpStatus.MULTI_STATUS, e);
        }
    }

    //Retrieve all offers from DB
    @GetMapping("/offers")
    public ResponseEntity<Object> getOffers() {
        try {
            return responseHandlerService.generateResponse("", HttpStatus.OK, offerService.getOffers());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Fetching offers failed", HttpStatus.MULTI_STATUS, e);
        }
    }

    //Add new offer (Now using DTO)
    @PostMapping("/offers/add")
    public ResponseEntity<Object> postOffer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestBody OfferDTO offerDTO) {
        try {
            offerService.postOffer(authHeader, offerDTO);
            return responseHandlerService.generateResponse("Offer posted successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while posting your advert", HttpStatus.MULTI_STATUS, e);
        }
    }


    //Edits exising offer
    @PatchMapping("/offers/{id}/edit")
    public ResponseEntity<Object> editOffer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable("id") Long id,
            @RequestBody OfferDTO editedOfferDTO) {
        try {
            offerService.editOffer(authHeader, id, editedOfferDTO);
            return responseHandlerService.generateResponse("Offer edited successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while editing your advert", HttpStatus.MULTI_STATUS, e);
        }
    }


    //Deletes offer (Now using DTO)
    @DeleteMapping("/offers/{id}/delete")
    public ResponseEntity<Object> deleteOffer(@PathVariable("id") Long id) {
        try {
            offerService.deleteOffer(id);
            return responseHandlerService.generateResponse("Offer deleted successfully", HttpStatus.NO_CONTENT,null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while deleting your advert", HttpStatus.MULTI_STATUS,e);
        }
    }

}
