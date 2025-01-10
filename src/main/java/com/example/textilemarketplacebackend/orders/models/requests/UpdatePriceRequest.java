package com.example.textilemarketplacebackend.orders.models.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePriceRequest {

    @NotNull(message = "Field price is required")
    @Min(value = 1, message = "Price must be greater than one")
    private Double price;
    private String message;
}
