package com.example.textilemarketplacebackend.db.models;

import com.example.textilemarketplacebackend.orders.models.LocalOrderDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "local_order")
public class LocalOrder extends LocalOrderDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offerId;

    @Column(name = "counteroffer", length = 512)
    private String counteroffer;

    @Enumerated
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

}