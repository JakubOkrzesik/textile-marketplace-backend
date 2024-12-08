package com.example.textilemarketplacebackend.orders.models;

import com.example.textilemarketplacebackend.db.models.LocalOrder;
import com.example.textilemarketplacebackend.db.models.Offer;
import com.example.textilemarketplacebackend.db.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalOrderDTO {

    private Long id;
    private Integer orderQuantity;
    private Long userId;
    private Offer offerId;
    private String counteroffer;
    private OrderStatus orderStatus;

    public LocalOrderDTO(LocalOrder localOrder) {
        this.id = localOrder.getId();
        this.orderQuantity = localOrder.getOrderQuantity();
        this.userId = (localOrder.getUser() != null) ? localOrder.getUser().getId() : null;
        this.offerId = localOrder.getOfferId();
        this.counteroffer = localOrder.getCounteroffer();
        this.orderStatus = localOrder.getOrderStatus();
    }

}
