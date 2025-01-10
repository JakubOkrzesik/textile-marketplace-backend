package com.example.textilemarketplacebackend.products.models.DTOs;

import com.example.textilemarketplacebackend.orders.models.DTOs.MessageDTO;
import com.example.textilemarketplacebackend.orders.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyerSellerDTO {
    // the purpose of this dto is to provide data to sellers and buyers about their products and subsequent orders
    // needs overhauls
    private String listingName;
    private String productImage;
    private Long id; // id of an order tied to the product
    private int listingQuantity;
    private int orderQuantity;
    private Long listingId; // id of the actual product
    private double newOrderPrice; // current price based on negotiations
    private double oldOrderPrice; // original price set by the seller
    private OrderStatus orderStatus;
    private List<MessageDTO> messages;
}
