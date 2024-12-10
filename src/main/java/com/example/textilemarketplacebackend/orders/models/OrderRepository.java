package com.example.textilemarketplacebackend.orders.models;

import com.example.textilemarketplacebackend.db.models.LocalOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;


public interface OrderRepository extends JpaRepository<LocalOrder, Long> {
    @Query("SELECT o FROM LocalOrder o")
    List<LocalOrder> getAllOrders();

}
