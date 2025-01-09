package com.example.textilemarketplacebackend.orders.services;

import com.example.textilemarketplacebackend.global.services.UserService;
import com.example.textilemarketplacebackend.mail.models.MailRequest;
import com.example.textilemarketplacebackend.mail.models.MailRequestType;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import com.example.textilemarketplacebackend.orders.models.*;
import com.example.textilemarketplacebackend.products.models.DTOs.BuyerSellerDTO;
import com.example.textilemarketplacebackend.products.models.ProductListing;
import com.example.textilemarketplacebackend.auth.models.user.User;
import com.example.textilemarketplacebackend.products.models.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ProductRepository productRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    // Lists all orders and returns them as DTOs
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    // Finds specific order by ID
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No order found with this Id"));
        return modelMapper.map(order, OrderDTO.class); // Returns DTO instead of Order object
    }

    public List<BuyerSellerDTO> getAllUserOrders(String authHeader) {
        User user = userService.extractUserFromToken(authHeader);

        return orderRepository.findAllByBuyer(user).stream().map(order -> BuyerSellerDTO.builder()
                .listingName(order.getProductListing().getProductName())
                .productImage(order.getProductListing().getImages() != null && order.getProductListing().getImages().isEmpty() ? order.getProductListing().getImages().getFirst() : null)
                .id(order.getId())
                .listingQuantity(order.getProductListing().getQuantity())
                .orderQuantity(order.getOrderQuantity())
                .listingId(order.getProductListing().getId())
                .newOrderPrice(order.getNewOrderPrice())
                .oldOrderPrice(order.getProductListing().getPrice())
                .orderStatus(order.getOrderStatus())
                .build())
                .toList();
    }

    // Creates an order from a product and returns the created order as DTO
    @Transactional
    public OrderDTO createOrderFromProduct(String authHeader, OrderDTO orderDTO) {

        User user = userService.extractUserFromToken(authHeader);
        int quantity = orderDTO.getOrderQuantity();
        double price = orderDTO.getNewOrderPrice();

        List<MailRequest> mailRequestList = new ArrayList<>();

        // Checking if product listing with that id exists
        ProductListing productListing = productRepository.findById(orderDTO.getListingId())
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));

        if (productListing.getUser().getId().equals(user.getId())) {
            throw new SelfPurchaseNotAllowedException("Seller cannot purchase their own products.");
        }

        if (productListing.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        // Create an order
        Order order = Order.builder()
                .buyer(user)
                .productListing(productListing)
                .newOrderPrice(price)
                .orderQuantity(quantity)
                .orderStatus(OrderStatus.PENDING)
                .build();

        OrderDTO orderDto = OrderDTO.builder()
                .id(order.getId())
                .buyerId(orderDTO.getBuyerId())
                .sellerId(order.getProductListing().getUser().getId())
                .orderQuantity(order.getOrderQuantity())
                .orderStatus(order.getOrderStatus())
                .newOrderPrice(order.getNewOrderPrice())
                .build();


        // Save and return the order as DTO
        orderRepository.save(order);

        // Storage update
        // Necessary due to the requirement of product stock reservation
        productListing.setQuantity(productListing.getQuantity() - quantity);
        productRepository.save(productListing);

        MailRequest orderMailRequest = MailRequest.builder()
                .body(String.format("You have created a new order for the product named: %s", productListing.getProductName()))
                .type(MailRequestType.NOTIFICATION)
                .recipients(new String[]{order.getBuyer().getEmail()})
                .build();

        mailRequestList.add(orderMailRequest);

        emailService.sendEmail(mailRequestList);

        return orderDto;
    }

    //
    public void updateOrderStatus(String authHeader, Long orderId, OrderStatus status) {

        if (status == null) {
            throw new IllegalArgumentException("Invalid order status type");
        }

        User user = userService.extractUserFromToken(authHeader);
        Long userId = user.getId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
        List<MailRequest> mailRequestList = new ArrayList<>();
        String buyerEmailBody;
        String sellerEmailBody;

        User seller = order.getProductListing().getUser();
        User buyer = order.getBuyer();

        switch (status) {
            case ACCEPTED -> {
                if (!seller.getId().equals(userId)) {
                    throw new InvalidOrderStatusException("You cannot accept the offer not being the seller");
                }
                buyerEmailBody = String.format("Congratulations! Your order with the ID: %d has been accepted and is now being processed", order.getId());
                sellerEmailBody = String.format("Congratulations! Your listing with the ID: %d has been sold", order.getId());
            }
            case PENDING, REJECTED, NEGOTIATION -> {
                if (!seller.getId().equals(userId) && !buyer.getId().equals(userId)) {
                    throw new InvalidOrderStatusException("Invalid order status type");
                }
                buyerEmailBody = String.format("Your order with the ID: %d has changed status to %s", order.getId(), status);
                sellerEmailBody = String.format("Your listing with the ID: %d has changed status to %s", order.getId(), status);
            }
            case null, default -> throw new InvalidOrderStatusException("Invalid order status type");
        }

        MailRequest buyerMailRequest = MailRequest.builder()
                .body(buyerEmailBody)
                .type(MailRequestType.NOTIFICATION)
                .recipients(new String[]{buyer.getEmail()})
                .build();

        MailRequest sellerMailRequest = MailRequest.builder()
                .body(sellerEmailBody)
                .type(MailRequestType.NOTIFICATION)
                .recipients(new String[]{seller.getEmail()})
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
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        order.setNewOrderPrice(newPrice);
        order.setOrderStatus(OrderStatus.NEGOTIATION);
        orderRepository.save(order);
    }

//    public void deleteOrder(Long id) {
//        orderRepository.deleteById(id);
//    }
//Perhaps we will need it later

}
