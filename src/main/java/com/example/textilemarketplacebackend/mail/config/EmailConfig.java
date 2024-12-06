package com.example.textilemarketplacebackend.mail.config;

import com.example.textilemarketplacebackend.mail.models.InvalidMailRequestException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Configuration
public class EmailConfig {
    @Bean
    public RestClient restClient() throws InvalidMailRequestException {
        return RestClient.builder()
                .baseUrl("mail-service-url/email/send")
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    String message = response.getBody().toString();
                    throw new InvalidMailRequestException(message);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    String message = response.getBody().toString();
                    throw new InvalidMailRequestException(message);
                })
                .build();
    }
}
