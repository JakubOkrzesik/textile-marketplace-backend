package com.example.textilemarketplacebackend.mail.config;

import com.example.textilemarketplacebackend.global.services.EnvService;
import com.example.textilemarketplacebackend.mail.models.InvalidMailRequestException;
import com.example.textilemarketplacebackend.mail.models.MailResponseWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EnvService envService;

    @Bean
    public RestClient restClient() throws InvalidMailRequestException {
        return RestClient.builder()
                .baseUrl(envService.getEMAIL_SERVICE_URL())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    handleErrorResponse(response);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    handleErrorResponse(response);
                })
                .build();
    }

    private void handleErrorResponse(ClientHttpResponse response) throws InvalidMailRequestException {
        try {
            String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
            MailResponseWrapper<String> errorResponse = objectMapper.readValue(responseBody, new TypeReference<>() {});
            throw new InvalidMailRequestException(errorResponse.getData());
        } catch (IOException e) {
            throw new InvalidMailRequestException("Error reading response body: " + e.getMessage());
        }
    }
}
