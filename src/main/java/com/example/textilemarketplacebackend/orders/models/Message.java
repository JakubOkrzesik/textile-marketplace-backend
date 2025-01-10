package com.example.textilemarketplacebackend.orders.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "sender", nullable = false)
    private OrderEnum sender;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "date", nullable = false)
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

}
