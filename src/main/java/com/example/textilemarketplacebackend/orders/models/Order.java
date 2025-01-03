package com.example.textilemarketplacebackend.orders.models;

import com.example.textilemarketplacebackend.auth.models.user.User;
import com.example.textilemarketplacebackend.listings.models.ProductListing;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "local_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long id;

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;

    // The buyer of the order
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    @JsonBackReference
    private User buyer;

    // The seller of the product/listing
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonBackReference
    private User seller;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private ProductListing productListing;

    @Column(name = "new_order_price", length = 512)
    private Double newOrderPrice;

    @Enumerated
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

}