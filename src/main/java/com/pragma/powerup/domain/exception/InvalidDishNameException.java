package com.pragma.powerup.domain.exception;

public class InvalidDishNameException extends RuntimeException {
    public InvalidDishNameException() {
        super("Invalid dish name");
    }
}
