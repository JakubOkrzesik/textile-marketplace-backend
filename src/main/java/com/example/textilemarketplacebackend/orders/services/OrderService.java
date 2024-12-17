package com.example.textilemarketplacebackend.orders.services;

import com.example.textilemarketplacebackend.global.services.UserService;
import com.example.textilemarketplacebackend.mail.models.MailRequest;
import com.example.textilemarketplacebackend.mail.models.MailRequestType;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import com.example.textilemarketplacebackend.orders.models.*;
import com.example.textilemarketplacebackend.offers.models.ProductListing;
import com.example.textilemarketplacebackend.auth.models.user.User;
import com.example.textilemarketplacebackend.offers.models.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ListingRepository listingRepository;
    private final UserService userService;
    private final EmailService emailService;

    // Lists all orders and returns them as DTOs
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    // Finds specific order by ID
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No order found with this Id"));
        return new OrderDTO(order); // Returns DTO instead of Order object
    }


    // Creates an order from an offer and returns the created order as DTO
    @Transactional
    public OrderDTO createOrderFromOffer(String authHeader, OrderDTO orderDTO) {

        User user = userService.extractUserFromToken(authHeader);
        int quantity = orderDTO.getOrderQuantity();
        double price = orderDTO.getNewOrderPrice();

        // Checking if product listing with that id exists
        ProductListing productListing = listingRepository.findById(orderDTO.getListingId())
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        if (productListing.getUser().getId().equals(user.getId())) {
            throw new SelfPurchaseNotAllowedException("Seller cannot purchase their own products.");
        }

        if (productListing.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        // Create an order
        // Setting the buyer and seller so that both parties can modify the price and status of the listing
        Order order = Order.builder()
                .buyer(user)
                .seller(productListing.getUser())
                .productListing(productListing)
                .newOrderPrice(price)
                .orderQuantity(quantity)
                .orderStatus(OrderStatus.PENDING)
                .build();


        // Save and return the order as DTO
        Order savedOrder = orderRepository.save(order);

        // Storage update
        // Necessary due to the requirement of product stock reservation
        productListing.setQuantity(productListing.getQuantity() - quantity);
        listingRepository.save(productListing);

        return new OrderDTO(savedOrder);
    }

    //
    public void updateOrderStatus(String authHeader, Long orderId, OrderStatus status) {

        if (status == null) {
            throw new IllegalArgumentException("Invalid order status type");
        }

        User user = userService.extractUserFromToken(authHeader);
        Long userId = user.getId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        List<MailRequest> mailRequestList = new ArrayList<>();
        String buyerEmailBody;
        String sellerEmailBody;

        switch (status) {
            case ACCEPTED -> {
                if (!order.getSeller().getId().equals(userId)) {
                    throw new IllegalArgumentException("Invalid order status type");
                }
                buyerEmailBody = String.format("Congratulations! Your order with the ID: %d has been accepted and is now being processed", order.getId());
                sellerEmailBody = String.format("Congratulations! Your listing with the ID: %d has been sold", order.getId());
            }
            case PENDING, REJECTED, NEGOTIATION -> {
                if (!order.getSeller().getId().equals(userId) && !order.getBuyer().getId().equals(userId)) {
                    throw new IllegalArgumentException("Invalid order status type");
                }
                buyerEmailBody = String.format("Your order with the ID: %d has changed status to %s", order.getId(), status);
                sellerEmailBody = String.format("Your listing with the ID: %d has changed status to %s", order.getId(), status);
            }
            case null, default -> throw new IllegalArgumentException("Invalid order status type");
        }

        MailRequest buyerMailRequest = MailRequest.builder()
                .body(buyerEmailBody)
                .type(MailRequestType.ACCOUNT_ACTIVATION)
                .recipients(new String[]{order.getBuyer().getEmail()})
                .build();

        MailRequest sellerMailRequest = MailRequest.builder()
                .body(sellerEmailBody)
                .type(MailRequestType.ACCOUNT_ACTIVATION)
                .recipients(new String[]{order.getSeller().getEmail()})
                .build();


        mailRequestList.add(buyerMailRequest);
        mailRequestList.add(sellerMailRequest);

        order.setOrderStatus(status);
        orderRepository.save(order);
        // email service send
        emailService.sendEmail(mailRequestList);
    }

    public void changeOrderPrice(Long orderId, Double newPrice) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setNewOrderPrice(newPrice);
        order.setOrderStatus(OrderStatus.NEGOTIATION);
        orderRepository.save(order);
    }

//    public void deleteOrder(Long id) {
//        orderRepository.deleteById(id);
//    }
//Perhaps we will need it later

}
