package com.example.textilemarketplacebackend.orders.services;

import com.example.textilemarketplacebackend.auth.models.user.UserRepository;
import com.example.textilemarketplacebackend.db.models.LocalOrder;
import com.example.textilemarketplacebackend.db.models.Offer;
import com.example.textilemarketplacebackend.db.models.OrderStatus;
import com.example.textilemarketplacebackend.db.models.LocalUser;
import com.example.textilemarketplacebackend.offers.models.OfferRepository;
import com.example.textilemarketplacebackend.orders.models.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // Lists all orders
    public List<LocalOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    // Finds specific order by ID
    public LocalOrder getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // When You click on some offer that interest You it becomes and order with specific enum value
    public LocalOrder createOrderFromOffer(Long userId, Long offerId, Integer quantity) {
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

        return orderRepository.save(order);
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
