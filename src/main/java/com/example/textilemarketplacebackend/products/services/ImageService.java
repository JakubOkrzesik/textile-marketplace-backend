package com.example.textilemarketplacebackend.products.services;

import com.example.textilemarketplacebackend.products.models.requests.ImageServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class ImageService {

    private final RestClient imageRestClient;

    public ImageServiceResponse uploadImage(MultipartFile file) throws IOException {
        // Extract file extension (e.g., png, jpeg)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File must have a name");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!fileExtension.equals("png") && !fileExtension.equals("jpeg") && !fileExtension.equals("jpg")) {
            throw new IllegalArgumentException("Unsupported file type. Only PNG and JPEG are allowed.");
        }

        // Dynamically set Content-Type based on file extension
        MediaType contentType = fileExtension.equals("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;

        return imageRestClient.put()
                .body(file.getBytes())  // Set the binary file body
                .contentType(contentType) // Set the correct content type
                .accept(MediaType.APPLICATION_JSON) // Expect JSON response
                .retrieve()
                .body(ImageServiceResponse.class);
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("Invalid file name: " + filename);
        }
        return filename.substring(lastDotIndex + 1);
    }
}
