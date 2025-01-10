package com.example.textilemarketplacebackend.orders.models.DTOs;

import com.example.textilemarketplacebackend.orders.models.OrderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String message;
    private OrderEnum sender;
    private Double price;
    private Date date;
}
