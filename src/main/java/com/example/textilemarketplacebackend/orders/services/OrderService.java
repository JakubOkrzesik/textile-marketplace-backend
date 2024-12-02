package com.example.textilemarketplacebackend.orders.services;

import com.example.textilemarketplacebackend.db.models.LocalOrder;
import com.example.textilemarketplacebackend.db.models.Offer;
import com.example.textilemarketplacebackend.offers.models.OfferRepository;
import com.example.textilemarketplacebackend.orders.models.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OfferRepository offerRepository;

    public OrderService(OrderRepository orderRepository, OfferRepository offerRepository) {
        this.orderRepository = orderRepository;
        this.offerRepository = offerRepository;
    }

    public List<LocalOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public LocalOrder getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public LocalOrder createOrder(LocalOrder order) {
        Offer offer = offerRepository.findById(order.getOfferId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        if (offer.getQuantity() < order.getOrderQuantity()) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        // Aktualizacja magazynu
        offer.setQuantity(offer.getQuantity() - order.getOrderQuantity());
        offerRepository.save(offer);

        // Zapis zamówienia
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
