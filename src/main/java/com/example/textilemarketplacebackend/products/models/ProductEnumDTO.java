package com.example.textilemarketplacebackend.products.models;

import com.example.textilemarketplacebackend.products.models.productEnums.FabricComposition;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricSafetyRequirements;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricTechnology;
import com.example.textilemarketplacebackend.products.models.productEnums.FabricType;
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
}
