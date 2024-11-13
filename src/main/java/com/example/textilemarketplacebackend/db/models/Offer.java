package com.example.textilemarketplacebackend.db.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer offerId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer productQuantity;

    @Column(nullable = false)
    private BigDecimal productPrice;  // Użycie BigDecimal zamiast Double

    @Column(columnDefinition = "TEXT")
    private String productDescription;
}
