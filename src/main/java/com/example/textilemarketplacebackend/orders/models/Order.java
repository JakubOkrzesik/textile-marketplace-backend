package com.example.textilemarketplacebackend.orders.models;

import com.example.textilemarketplacebackend.auth.models.user.User;
import com.example.textilemarketplacebackend.products.models.ProductListing;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private ProductListing productListing;

    @Column(name = "new_order_price", length = 512)
    private Double newOrderPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messageList = new ArrayList<>();

    @Enumerated
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;
}