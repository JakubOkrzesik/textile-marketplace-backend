package com.example.textilemarketplacebackend.listings.models;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingDTO {
    private Long id;
    private String productName;
    private String shortDescription;

    @NotNull(message = "Long description must not be null")
    @NotBlank(message = "Long description must not be blank")
    @Size(max = 1024, message = "Long description must not be longer than 1024 characters")
    private String longDescription;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be greater than one")
    private Double price;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Quantity must be greater than one")
    private Integer quantity;
}
