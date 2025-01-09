package com.example.textilemarketplacebackend.listings.controllers;

import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.listings.models.DeleteImageRequest;
import com.example.textilemarketplacebackend.listings.models.ImageServiceResponse;
import com.example.textilemarketplacebackend.listings.models.InvalidImageRequestException;
import com.example.textilemarketplacebackend.listings.services.ImageService;
import com.example.textilemarketplacebackend.listings.services.StorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/image")
public class ImageUploadController {

    private final ImageService imageService;
    private final ResponseHandlerService responseHandlerService;
    private final StorageService storageService;
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

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteImage(@RequestParam String filename) {
        try {
            storageService.deleteItem(filename);
            return responseHandlerService.generateResponse("Image deleted", HttpStatus.NO_CONTENT, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/delete_images")
    public ResponseEntity<Object> deleteImages(@RequestParam String[] filenames) {
        try {
            storageService.deleteItems(filenames);
            return responseHandlerService.generateResponse("Images deleted", HttpStatus.NO_CONTENT, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
