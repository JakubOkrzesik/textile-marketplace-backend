package com.example.textilemarketplacebackend.offers.models;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingDTO {
    private Long id;
    private String productName;
    private String shortDescription;

    @NotNull(message = "Long description must not be null")
    @NotBlank(message = "Long description must not be blank")
    @Max(value = 1024, message = "Long description must not be longer than 1024 characters")
    private String longDescription;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be greater than one")
    private Double price;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Quantity must be greater than one")
    private Integer quantity;

    public ListingDTO(ProductListing productListing) {
        this.id = productListing.getId();
        this.productName = productListing.getProductName();
        this.shortDescription = productListing.getShortDescription();
        this.longDescription = productListing.getLongDescription();
        this.price = productListing.getPrice();
        this.quantity = productListing.getQuantity();
    }
}
