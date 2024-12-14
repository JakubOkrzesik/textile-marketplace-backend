package com.example.textilemarketplacebackend.mail.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailResponseWrapper<T> {
    private T data;
    private String message;
    private int status;
}
