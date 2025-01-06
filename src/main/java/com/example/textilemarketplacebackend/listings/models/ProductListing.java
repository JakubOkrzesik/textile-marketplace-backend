package com.example.textilemarketplacebackend.listings.models;

import com.example.textilemarketplacebackend.listings.models.productEnums.FabricComposition;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricSafetyRequirements;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricTechnology;
import com.example.textilemarketplacebackend.listings.models.productEnums.FabricType;
import com.example.textilemarketplacebackend.listings.services.StringListConverter;
import com.example.textilemarketplacebackend.orders.models.Order;
import com.example.textilemarketplacebackend.auth.models.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ProductListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description", nullable = false, length = 1024)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Convert(converter = StringListConverter.class)
    @Column(name = "image_urls", length = 4)
    private List<String> images = new ArrayList<>();

    @Column(name = "fabric_type", nullable = false)
    private FabricType fabricType;

    @Column(name = "fabric_composition", nullable = false)
    private FabricComposition composition;

    @Column(name = "fabric_technology", nullable = false)
    private FabricTechnology technologies;

    @Column(name = "fabric_safety_requirements", nullable = false)
    private FabricSafetyRequirements safetyRequirements;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "productListing", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

}