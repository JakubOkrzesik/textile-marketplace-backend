package com.example.textilemarketplacebackend.orders.models;

import com.example.textilemarketplacebackend.db.models.LocalOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<LocalOrder, Long> {
}
