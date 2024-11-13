package com.example.textilemarketplacebackend.db.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(length = 255)
    private String name;

    @Column(length = 10, nullable = false)
    private String nip;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    // Relacje
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Offer> offers;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    private List<Order> purchases;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Order> sales;
}
