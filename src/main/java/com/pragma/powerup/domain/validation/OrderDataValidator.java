package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.InvalidOrderException;
import com.pragma.powerup.domain.model.Order;

public class OrderDataValidator {

    public void validateForCreate(Order order) {
        if (order == null) {
            throw new InvalidOrderException("Order is required");
        }
        if (order.getRestaurantId() == null || order.getRestaurantId() <= 0) {
            throw new InvalidOrderException("Restaurant id is invalid");
        }
        if (order.getClientId() == null || order.getClientId() <= 0) {
            throw new InvalidOrderException("Client id is invalid");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }

        order.getItems().forEach(item -> {
            if (item.getDishId() == null || item.getDishId() <= 0) {
                throw new InvalidOrderException("Dish id is invalid");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new InvalidOrderException("Quantity must be greater than zero");
            }
        });
    }
}
