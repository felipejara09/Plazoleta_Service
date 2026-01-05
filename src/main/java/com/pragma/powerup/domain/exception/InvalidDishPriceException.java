package com.pragma.powerup.domain.exception;

public class InvalidDishPriceException extends DomainException {
    public InvalidDishPriceException() {
        super("Invalid dish price");
    }
}
