package com.pragma.powerup.domain.exception;

public class InvalidRestaurantNameException extends RuntimeException {
    public InvalidRestaurantNameException() {
        super("Restaurant name cannot be only numbers");
    }
}
