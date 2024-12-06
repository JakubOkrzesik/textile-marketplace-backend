package com.example.textilemarketplacebackend.mail.services;

import com.example.textilemarketplacebackend.mail.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final RestClient restClient;

    public MailResponseWrapper sendEmail(List<MailRequest> mailRequestList) throws InvalidMailRequestException, InternalMailServiceErrorException {
        MailResponseWrapper response = restClient.post()
                .body(mailRequestList)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(MailResponseWrapper.class);

        if (response == null) {
            throw new IllegalStateException ("Failed to map the response");
        }

        return response;
    }
}
