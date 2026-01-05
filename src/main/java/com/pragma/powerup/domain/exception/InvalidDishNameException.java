package com.pragma.powerup.domain.exception;

public class InvalidDishNameException extends DomainException {
    public InvalidDishNameException() {
        super("Invalid dish name");
    }
}
