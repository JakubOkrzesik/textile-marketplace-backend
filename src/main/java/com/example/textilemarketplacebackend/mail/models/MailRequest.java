package com.example.textilemarketplacebackend.mail.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {
    private String[] recipients;
    private String body;
    private MailRequestType type;
    private String url;
}
