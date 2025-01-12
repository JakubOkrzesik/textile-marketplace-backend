package com.example.textilemarketplacebackend.products.models.DTOs;

import com.example.textilemarketplacebackend.products.models.productEnums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEnumDTO {
    private FabricType[] fabricTypes;
    private FabricComposition[] compositions;
    private FabricTechnology[] technologies;
    private FabricSafetyRequirements[] safetyRequirements;
    private ExporterEnum[] exporters;
    private OriginalProductName[] originalProductNames;
}
