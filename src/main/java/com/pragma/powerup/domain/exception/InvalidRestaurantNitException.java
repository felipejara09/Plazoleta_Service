package com.pragma.powerup.domain.exception;

public class InvalidRestaurantNitException extends RuntimeException {
    public InvalidRestaurantNitException() {
        super("Invalid NIT format");
    }
}
