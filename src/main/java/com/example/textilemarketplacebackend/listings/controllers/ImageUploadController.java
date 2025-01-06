package com.example.textilemarketplacebackend.listings.controllers;

import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.listings.models.ImageRequest;
import com.example.textilemarketplacebackend.listings.models.InvalidImageRequestException;
import com.example.textilemarketplacebackend.listings.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/image")
public class ImageUploadController {

    private final ImageService imageService;
    private final ResponseHandlerService responseHandlerService;

    @PutMapping("/upload")
    public ResponseEntity<Object> uploadImage(@RequestBody ImageRequest imageReuqest) {
        try {
            return responseHandlerService.generateResponse("Image has been successfully uploaded", HttpStatus.OK, imageService.uploadImage(imageReuqest));
        } catch (InvalidImageRequestException e) {
            return responseHandlerService.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return responseHandlerService.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
