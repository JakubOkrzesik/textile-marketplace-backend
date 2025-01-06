package com.example.textilemarketplacebackend.listings.services;

import com.example.textilemarketplacebackend.listings.models.ImageRequest;
import com.example.textilemarketplacebackend.listings.models.ImageServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
@RequiredArgsConstructor
public class ImageService {

    private final RestClient imageRestClient;

    public ImageServiceResponse uploadImage(ImageRequest imageRequest) {
        return imageRestClient.put()
                .body(imageRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ImageServiceResponse.class);
    }
}
