package com.example.textilemarketplacebackend.listings.models;

import com.example.textilemarketplacebackend.listings.models.productEnums.FabricComposition;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricSafetyRequirements;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricTechnology;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListingEnumDTO {
    private FabricType[] fabricTypes;
    private FabricComposition[] compositions;
    private FabricTechnology[] technologies;
    private FabricSafetyRequirements[] safetyRequirements;
}
