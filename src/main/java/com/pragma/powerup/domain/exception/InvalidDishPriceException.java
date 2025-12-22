package com.pragma.powerup.domain.exception;

public class InvalidDishPriceException extends RuntimeException {
    public InvalidDishPriceException() {
        super("Invalid dish price");
    }
}
