package com.example.textilemarketplacebackend.products.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageServiceResponse {
    private String body;
    private String statusCode;
}
