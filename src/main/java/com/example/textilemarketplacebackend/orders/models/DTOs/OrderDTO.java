package com.example.textilemarketplacebackend.orders.models.DTOs;

import com.example.textilemarketplacebackend.orders.models.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    private Long id;

    @NotNull(message = "Order quantity is required")
    @Min(value = 1, message = "Order quantity must be greater than one")
    private Integer orderQuantity;

    @NotNull(message = "Listing id must not be null")
    private Long listingId;

    private Long buyerId;

    private Long sellerId;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be greater than one")
    private Double newOrderPrice;

    private Double oldOrderPrice;

    private List<MessageDTO> messages;

    private OrderStatus orderStatus;
}
