package com.example.textilemarketplacebackend.offers.models;


import com.example.textilemarketplacebackend.db.models.Offer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO extends Offer {
    private Long id;
    private String productName;
    private String shortDescription;
    private String longDescription;
    private Double price;
    private Integer quantity;
    private Long userId;

    public OfferDTO(Offer offer) {
        this.id = offer.getId();
        this.productName = offer.getProductName();
        this.shortDescription = offer.getShortDescription();
        this.longDescription = offer.getLongDescription();
        this.price = offer.getPrice();
        this.quantity = offer.getQuantity();
        this.userId = offer.getUser().getId();
    }
}
