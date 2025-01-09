package com.example.textilemarketplacebackend.orders.models;

import com.example.textilemarketplacebackend.auth.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByBuyer(User buyer);
}
