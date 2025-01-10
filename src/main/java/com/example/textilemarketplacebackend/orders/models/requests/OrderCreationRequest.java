package com.example.textilemarketplacebackend.orders.models.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreationRequest {

    @NotNull(message = "Order quantity is required")
    @Min(value = 1, message = "Order quantity must be greater than one")
    private Integer orderQuantity;

    @NotNull(message = "Listing id must not be null")
    private Long listingId;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be greater than one")
    private Double price;

    private String message;
}
