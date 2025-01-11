package com.example.textilemarketplacebackend.auth.models.user;

import com.example.textilemarketplacebackend.products.models.DTOs.ProductDTO;
import com.example.textilemarketplacebackend.orders.models.DTOs.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String nip;
    private List<ProductDTO> productListings;
    private List<OrderDTO> ordersAsSeller;
    private List<OrderDTO> ordersAsBuyer;
    private SubscriptionDTO subscription;
}
