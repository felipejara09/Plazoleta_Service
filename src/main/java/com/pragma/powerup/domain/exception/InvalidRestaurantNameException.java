package com.pragma.powerup.domain.exception;

public class InvalidRestaurantNameException extends DomainException {
    public InvalidRestaurantNameException() {
        super("Restaurant name cannot be only numbers");
    }
}
