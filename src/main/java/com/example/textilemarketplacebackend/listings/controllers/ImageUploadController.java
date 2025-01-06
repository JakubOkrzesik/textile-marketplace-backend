package com.example.textilemarketplacebackend.listings.controllers;

import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.listings.models.ImageServiceResponse;
import com.example.textilemarketplacebackend.listings.models.InvalidImageRequestException;
import com.example.textilemarketplacebackend.listings.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/image")
public class ImageUploadController {

    private final ImageService imageService;
    private final ResponseHandlerService responseHandlerService;
    Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    @PutMapping(path = "/upload")
    public ResponseEntity<Object> uploadImage(@RequestPart MultipartFile file) {
        try {
            ImageServiceResponse response = imageService.uploadImage(file);
            logger.info(response.toString());
            return ResponseEntity.ok(response);
        } catch (InvalidImageRequestException e) {
            return responseHandlerService.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return responseHandlerService.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
