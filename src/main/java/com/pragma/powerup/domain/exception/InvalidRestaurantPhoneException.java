package com.pragma.powerup.domain.exception;

public class InvalidRestaurantPhoneException extends RuntimeException {
    public InvalidRestaurantPhoneException() {
        super("Invalid restaurant phone format");
    }
}
