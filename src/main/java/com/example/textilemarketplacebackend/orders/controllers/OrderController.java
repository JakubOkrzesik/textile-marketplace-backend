package com.example.textilemarketplacebackend.orders.controllers;

import com.example.textilemarketplacebackend.db.models.LocalOrder;
import com.example.textilemarketplacebackend.db.models.OrderStatus;
import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.orders.models.LocalOrderDTO;
import com.example.textilemarketplacebackend.orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ResponseHandlerService responseHandlerService;
    private final OrderService orderService;

    //Returns all orders now using DTO
    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllOrders() {
        try {
            return responseHandlerService.generateResponse("Orders fetched successfully", HttpStatus.OK, orderService.getAllOrders());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to fetch orders", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable Long id) {
        try {
            LocalOrder order = orderService.getOrderById(id);
            if (order == null) {
                return responseHandlerService.generateResponse("Order not found", HttpStatus.NOT_FOUND, null);
            }
            return responseHandlerService.generateResponse("Order fetched successfully", HttpStatus.OK, order);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to fetch order", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createOrderFromOffer(@RequestParam Long userId, @RequestParam Long offerId, @RequestParam Integer quantity) {
        try {
            // Zamiast pełnego obiektu, zwracamy DTO
            LocalOrderDTO orderDTO = orderService.createOrderFromOffer(userId, offerId, quantity);
            return responseHandlerService.generateResponse("Order created successfully", HttpStatus.CREATED, orderDTO);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to create order", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Object> acceptOrder(@PathVariable Long id) {
        try {
            orderService.updateOrderStatus(id, OrderStatus.ACCEPTED);
            return responseHandlerService.generateResponse("Order accepted successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to accept order", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Object> rejectOrder(@PathVariable Long id) {
        try {
            orderService.updateOrderStatus(id, OrderStatus.REJECTED);
            return responseHandlerService.generateResponse("Order rejected successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to reject order", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @PutMapping("/{id}/counteroffer")
    public ResponseEntity<Object> addCounteroffer(@PathVariable Long id, @RequestBody String counteroffer) {
        try {
            orderService.addCounteroffer(id, counteroffer);
            return responseHandlerService.generateResponse("Counteroffer added successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to add counteroffer", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @PutMapping("/{id}/accept-counteroffer")
    public ResponseEntity<Object> acceptCounteroffer(@PathVariable Long id) {
        try {
            orderService.updateOrderStatus(id, OrderStatus.ACCEPTED);
            return responseHandlerService.generateResponse("Counteroffer accepted successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to accept counteroffer", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @PutMapping("/{id}/reject-counteroffer")
    public ResponseEntity<Object> rejectCounteroffer(@PathVariable Long id) {
        try {
            orderService.updateOrderStatus(id, OrderStatus.REJECTED);
            return responseHandlerService.generateResponse("Counteroffer rejected successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to reject counteroffer", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
