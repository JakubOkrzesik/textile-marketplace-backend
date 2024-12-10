package com.example.textilemarketplacebackend.orders.models;

import com.example.textilemarketplacebackend.db.models.LocalOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<LocalOrder, Long> {
    @Query("SELECT o FROM LocalOrder o")
    List<LocalOrder> getAllOrders();

    @Query("SELECT o FROM LocalOrder o LEFT JOIN FETCH o.user WHERE o.id = :id")
    Optional<LocalOrder> findByIdWithUser(@Param("id") Long id);

}
