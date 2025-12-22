package com.pragma.powerup.domain.exception;

public class InvalidDishDescriptionException extends RuntimeException {
    public InvalidDishDescriptionException() {
        super("Invalid dish description");
    }
}