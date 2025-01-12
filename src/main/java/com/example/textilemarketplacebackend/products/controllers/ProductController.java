package com.example.textilemarketplacebackend.products.controllers;

import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;

import com.example.textilemarketplacebackend.products.models.DTOs.ProductDTO;
import com.example.textilemarketplacebackend.products.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ResponseHandlerService responseHandlerService;

    private final ProductService productService;

    //Add new offer (Now using DTO)
    @PostMapping("/add")
    public ResponseEntity<Object> postOffer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @Valid @RequestBody ProductDTO productDTO) {
        try {
            productService.postProduct(authHeader, productDTO);
            return responseHandlerService.generateResponse(String.format("Offer with the name %s has been posted successfully.", productDTO.getProductName()), HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while posting your advert", HttpStatus.MULTI_STATUS, e);
        }
    }

    //Fetch offers by designated ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOfferById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long id) {
        try {
            return responseHandlerService.generateResponse(String.format("Offer with id %d has been fetched", id), HttpStatus.OK, productService.getProductById(id, authHeader));
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Fetching offer failed", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    // Edits exising offer
    @PatchMapping("/{id}/edit")
    public ResponseEntity<Object> editOffer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductDTO editedProductDTO) {
        try {
            // we need to check if the offer actually belongs to the user
            productService.editProduct(authHeader, id, editedProductDTO);
            return responseHandlerService.generateResponse("Offer edited successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while editing your advert", HttpStatus.MULTI_STATUS, e.getMessage());
        }
    }


    // Deletes offer (Now using DTO)
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> deleteOffer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable("id") Long id) {
        try {
            productService.deleteProduct(authHeader, id);
            return responseHandlerService.generateResponse("Offer deleted successfully", HttpStatus.NO_CONTENT,null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while deleting your advert", HttpStatus.MULTI_STATUS,e.getMessage());
        }
    }

    @GetMapping("/get-user-products")
    public ResponseEntity<Object> getUserProducts(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            return responseHandlerService.generateResponse("User products fetched", HttpStatus.OK, productService.getUserProducts(authHeader));
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while fetching user products", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
