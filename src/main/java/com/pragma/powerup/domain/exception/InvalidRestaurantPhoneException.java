package com.pragma.powerup.domain.exception;

public class InvalidRestaurantPhoneException extends DomainException {
    public InvalidRestaurantPhoneException() {
        super("Invalid restaurant phone format");
    }
}
