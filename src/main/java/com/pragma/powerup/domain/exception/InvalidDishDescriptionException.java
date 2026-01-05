package com.pragma.powerup.domain.exception;

public class InvalidDishDescriptionException extends DomainException {
    public InvalidDishDescriptionException() {
        super("Invalid dish description");
    }
}