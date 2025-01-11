package com.example.textilemarketplacebackend.products.controllers;

import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.products.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/main-page-products")
@RequiredArgsConstructor
public class MainPageProductsController {
    // the purpose of this controller is to provide product data no matter if the user is logged in or not

    private final ResponseHandlerService responseHandlerService;
    private final ProductService productService;

    //Retrieve all offers from DB
    @GetMapping("/get_all")
    public ResponseEntity<Object> getOffers() {
        try {
            return responseHandlerService.generateResponse("Offers have been fetched", HttpStatus.OK, productService.getProducts());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Fetching offers failed", HttpStatus.MULTI_STATUS, e.getMessage());
        }
    }

    @GetMapping("/get-listing-enums")
    public ResponseEntity<Object> getListingEnums() {
        try {
            return responseHandlerService.generateResponse("Listing enums fetched", HttpStatus.OK, productService.getListingEnums());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("An error occurred while fetching listing enums", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
