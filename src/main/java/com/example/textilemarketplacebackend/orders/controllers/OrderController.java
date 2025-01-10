package com.example.textilemarketplacebackend.orders.controllers;

import com.example.textilemarketplacebackend.orders.models.OrderStatus;
import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.orders.models.DTOs.OrderDTO;
import com.example.textilemarketplacebackend.orders.models.SelfPurchaseNotAllowedException;
import com.example.textilemarketplacebackend.orders.models.requests.OrderCreationRequest;
import com.example.textilemarketplacebackend.orders.models.requests.UpdatePriceRequest;
import com.example.textilemarketplacebackend.orders.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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
            List<OrderDTO> orderList = orderService.getAllOrders();
            return responseHandlerService.generateResponse("Orders fetched successfully", HttpStatus.OK, orderList);
        } catch (IllegalArgumentException | NullPointerException e) {
            return responseHandlerService.generateResponse("Error while mapping orders", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to fetch orders", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/get-user-orders")
    public ResponseEntity<Object> getUserOrders(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            return responseHandlerService.generateResponse("Orders fetched successfully", HttpStatus.OK, orderService.getAllUserOrders(authHeader));
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to fetch orders", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable Long id) {
        try {
            OrderDTO orderDTO = orderService.getOrderById(id);
            return responseHandlerService.generateResponse("Order fetched successfully", HttpStatus.OK, orderDTO);
        } catch (NoSuchElementException e) {
            return responseHandlerService.generateResponse("Order with the provided id was not found", HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Fetching order failed", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @PostMapping("/create")
    public ResponseEntity<Object> createOrderFromProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @Valid @RequestBody OrderCreationRequest orderCreationRequest) {
        try {
            orderService.createOrderFromProduct(authHeader, orderCreationRequest);
            return responseHandlerService.generateResponse("Order created successfully", HttpStatus.CREATED, null);
        } catch (SelfPurchaseNotAllowedException e) {
            return responseHandlerService.generateResponse("Error while creating the order", HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to create order", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/{id}/change-price")
    public ResponseEntity<Object> changeOrderPrice(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long id, @Valid @RequestBody UpdatePriceRequest priceRequest) {
        try {
            // change price based on order id
            orderService.changeOrderPrice(authHeader, id, priceRequest);
            return responseHandlerService.generateResponse("Order price changed successfully", HttpStatus.OK, null);
        } catch (NoSuchElementException e) {
            return responseHandlerService.generateResponse("Order with the provided id was not found", HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to add counteroffer", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/{id}/change-order-status")
    public ResponseEntity<Object> updateOrderStatus(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long id, @RequestParam OrderStatus newStatus) {
        try {
            orderService.updateOrderStatus(authHeader, id, newStatus);
            return responseHandlerService.generateResponse("Order status changed successfully.", HttpStatus.OK, null);
        } catch (NoSuchElementException e) {
            return responseHandlerService.generateResponse("Order with the provided id was not found", HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to reject counteroffer", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<Object> acceptOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long id) {
        try {
            orderService.updateOrderStatus(authHeader, id, OrderStatus.ACCEPTED);
            return responseHandlerService.generateResponse("Order accepted successfully", HttpStatus.OK, null);
        } catch (NoSuchElementException e) {
            return responseHandlerService.generateResponse("Order with the provided id was not found", HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return responseHandlerService.generateResponse("User not valid to perform action", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to accept order", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @PutMapping("/{id}/reject")
    public ResponseEntity<Object> rejectOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long id) {
        try {
            orderService.updateOrderStatus(authHeader, id, OrderStatus.REJECTED);
            return responseHandlerService.generateResponse("Order rejected successfully", HttpStatus.OK, null);
        } catch (NoSuchElementException e) {
            return responseHandlerService.generateResponse("Order with the provided id was not found", HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return responseHandlerService.generateResponse("User not valid to perform action", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return responseHandlerService.generateResponse("Failed to reject order", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


}
