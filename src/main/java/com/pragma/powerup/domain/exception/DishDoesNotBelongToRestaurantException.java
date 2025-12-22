package com.pragma.powerup.domain.exception;

public class DishDoesNotBelongToRestaurantException extends DomainException {
    public DishDoesNotBelongToRestaurantException() {
        super("One or more dishes do not belong to the restaurant or are not active");
    }
}
