package com.pragma.powerup.domain.exception;

public class InvalidRestaurantNitException extends DomainException {
    public InvalidRestaurantNitException() {
        super("Invalid NIT format");
    }
}
