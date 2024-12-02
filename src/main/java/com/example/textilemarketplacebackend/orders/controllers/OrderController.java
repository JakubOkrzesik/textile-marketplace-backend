package com.example.textilemarketplacebackend.orders.controllers;

import com.example.textilemarketplacebackend.db.models.LocalOrder;
import com.example.textilemarketplacebackend.db.models.OrderStatus;
import com.example.textilemarketplacebackend.orders.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<LocalOrder>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalOrder> getOrderById(@PathVariable Long id) {
        LocalOrder order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping("/create")
    public ResponseEntity<LocalOrder> createOrderFromOffer(@RequestParam Long userId, @RequestParam Long offerId, @RequestParam Integer quantity) {
        LocalOrder order = orderService.createOrderFromOffer(userId, offerId, quantity);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptOrder(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.ACCEPTED);
        return ResponseEntity.ok("Order accepted.");
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectOrder(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.REJECTED);
        return ResponseEntity.ok("Order rejected.");
    }

    @PutMapping("/{id}/counteroffer")
    public ResponseEntity<?> addCounteroffer(@PathVariable Long id, @RequestBody String counteroffer) {
        orderService.addCounteroffer(id, counteroffer);
        return ResponseEntity.ok("Counteroffer added, status updated to negotiation.");
    }

    @PutMapping("/{id}/accept-counteroffer")
    public ResponseEntity<?> acceptCounteroffer(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.ACCEPTED);
        return ResponseEntity.ok("Counteroffer accepted, order completed.");
    }

    @PutMapping("/{id}/reject-counteroffer")
    public ResponseEntity<?> rejectCounteroffer(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.REJECTED);
        return ResponseEntity.ok("Counteroffer rejected, order closed.");
    }
}

