package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private Long restaurantId;
    private Long clientId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItem> items;
}
