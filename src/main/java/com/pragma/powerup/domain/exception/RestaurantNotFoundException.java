package com.pragma.powerup.domain.exception;

public class RestaurantNotFoundException extends DomainException {
    public RestaurantNotFoundException() {
        super("Restaurant not found");
    }
}