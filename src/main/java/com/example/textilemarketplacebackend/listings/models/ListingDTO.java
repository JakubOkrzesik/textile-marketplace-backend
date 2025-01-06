package com.example.textilemarketplacebackend.listings.models;


import com.example.textilemarketplacebackend.listings.models.productEnums.FabricComposition;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricSafetyRequirements;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricTechnology;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingDTO {
    private Long id;
    private String productName;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be blank")
    @Size(max = 1024, message = "Description must not be longer than 1024 characters")
    private String description;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be greater than one")
    private Double price;

    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be greater than one")
    private Integer quantity;

    private List<String> images;

    @NotNull(message = "Fabric type must not be null")
    private FabricType fabricType;

    @NotNull(message = "Fabric composition must not be null")
    private FabricComposition composition;

    @NotNull(message = "Fabric technology must not be null")
    private FabricTechnology technologies;

    @NotNull(message = "Fabric safety requirements must not be null")
    private FabricSafetyRequirements safetyRequirements;
}
