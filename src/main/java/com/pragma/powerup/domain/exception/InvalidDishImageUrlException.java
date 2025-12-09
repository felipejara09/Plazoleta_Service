package com.pragma.powerup.domain.exception;

public class InvalidDishImageUrlException extends RuntimeException {
    public InvalidDishImageUrlException() {
        super("Invalid dish image URL");
    }
}
