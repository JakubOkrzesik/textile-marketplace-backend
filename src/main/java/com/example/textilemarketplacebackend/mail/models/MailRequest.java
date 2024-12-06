package com.example.textilemarketplacebackend.mail.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {
    private String[] recipients;
    private String body;
    // maybe change to enum
    private String type;
    private String password_reset_url;
}
