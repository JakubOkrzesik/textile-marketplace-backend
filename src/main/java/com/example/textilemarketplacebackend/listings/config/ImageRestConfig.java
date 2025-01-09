package com.example.textilemarketplacebackend.listings.config;

import com.example.textilemarketplacebackend.global.services.EnvService;
import com.example.textilemarketplacebackend.listings.models.ImageServiceResponse;
import com.example.textilemarketplacebackend.listings.models.InvalidImageRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class ImageRestConfig {

    private final EnvService envService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public RestClient imageRestClient() {
        return RestClient.builder()
                .baseUrl(envService.getIMAGE_SERVICE_URL())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    handleErrorResponse(response);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    handleErrorResponse(response);
                })
                .build();
    }

    private void handleErrorResponse(ClientHttpResponse response) throws InvalidImageRequestException {
        try {
            String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
            ImageServiceResponse errorResponse = objectMapper.readValue(responseBody, ImageServiceResponse.class);
            throw new InvalidImageRequestException(errorResponse.getBody());
        } catch (IOException e) {
            throw new InvalidImageRequestException("Error reading response body: " + e.getMessage());
        }
    }
}
