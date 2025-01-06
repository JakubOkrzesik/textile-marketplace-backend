package com.example.textilemarketplacebackend.mail.services;

import com.example.textilemarketplacebackend.mail.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final RestClient emailRestClient;

    public MailResponseWrapper<List<MailResponse>> sendEmail(List<MailRequest> mailRequestList) throws InvalidMailRequestException, InternalMailServiceErrorException {
        MailResponseWrapper<List<MailResponse>> response = emailRestClient.post()
                .body(mailRequestList)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null) {
            throw new IllegalStateException ("Failed to map the response");
        }

        return response;
    }
}
