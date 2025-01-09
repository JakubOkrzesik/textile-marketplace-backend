package com.example.textilemarketplacebackend.products.models;

import com.example.textilemarketplacebackend.products.models.productEnums.FabricComposition;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricSafetyRequirements;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricTechnology;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String productName;

    @NotNull(message = "Short description must not be null")
    @NotBlank(message = "Short description must not be blank")
    @Size(max = 256, message = "Short description must not be longer than 256 characters")
    private String shortDescription;

    @NotNull(message = "Long description must not be null")
    @NotBlank(message = "Long description must not be blank")
    @Size(max = 2048, message = "Long description must not be longer than 2048 characters")
    private String longDescription;

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

    @NotBlank(message = "Colour must not be blank")
    private String colour;

    @NotBlank(message = "Width must not be blank")
    private String width;
}
