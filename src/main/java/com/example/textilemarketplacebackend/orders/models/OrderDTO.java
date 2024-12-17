package com.example.textilemarketplacebackend.orders.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long id;

    @NotNull(message = "Order quantity is required")
    @Min(value = 1, message = "Order quantity must be greater than one")
    private Integer orderQuantity;

    @NotNull(message = "Listing id must not be null")
    private Long listingId;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be greater than one")
    private Double newOrderPrice;

    private OrderStatus orderStatus;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.orderQuantity = order.getOrderQuantity();
        this.listingId = order.getProductListingId();
        this.newOrderPrice = order.getNewOrderPrice();
        this.orderStatus = order.getOrderStatus();
    }

}
