package com.pragma.powerup.domain.exception;

public class InvalidDishImageUrlException extends DomainException {
    public InvalidDishImageUrlException() {
        super("Invalid dish image URL");
    }
}
