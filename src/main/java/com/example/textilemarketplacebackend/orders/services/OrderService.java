package com.example.textilemarketplacebackend.orders.services;

import com.example.textilemarketplacebackend.auth.models.user.UserRepository;
import com.example.textilemarketplacebackend.db.models.LocalOrder;
import com.example.textilemarketplacebackend.db.models.Offer;
import com.example.textilemarketplacebackend.db.models.OrderStatus;
import com.example.textilemarketplacebackend.db.models.LocalUser;
import com.example.textilemarketplacebackend.offers.models.OfferRepository;
import com.example.textilemarketplacebackend.orders.models.LocalOrderDTO;
import com.example.textilemarketplacebackend.orders.models.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OfferRepository offerRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
    }

    // Lists all orders and returns them as DTOs
    public List<LocalOrderDTO> getAllOrders() {
        List<LocalOrder> orders = orderRepository.findAll();
        return orders.stream()
                .map(LocalOrderDTO::new)
                .collect(Collectors.toList());
    }

    // Finds specific order by ID
    public LocalOrder getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // Creates an order from an offer and returns the created order as DTO
    public LocalOrderDTO createOrderFromOffer(Long userId, Long offerId, Integer quantity) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        if (offer.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        LocalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Storage update
        offer.setQuantity(offer.getQuantity() - quantity);
        offerRepository.save(offer);

        // Create an order
        LocalOrder order = new LocalOrder();
        order.setUser(user);
        order.setOfferId(offer);
        order.setOrderQuantity(quantity);
        order.setOrderStatus(OrderStatus.PENDING);

        // Save and return the order as DTO
        LocalOrder savedOrder = orderRepository.save(order);
        return new LocalOrderDTO(savedOrder); // Zwracamy DTO
    }

    //
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        LocalOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    public void addCounteroffer(Long orderId, String counteroffer) {
        LocalOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setCounteroffer(counteroffer);
        order.setOrderStatus(OrderStatus.NEGOTIATION);
        orderRepository.save(order);
    }

//    public void deleteOrder(Long id) {
//        orderRepository.deleteById(id);
//    }
//Perhaps we will need it later

}
