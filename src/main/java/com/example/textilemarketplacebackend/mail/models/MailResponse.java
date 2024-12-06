package com.example.textilemarketplacebackend.mail.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailResponse {
    private String recipients;
    private String type;
    private String status;
    private String msg;
}
