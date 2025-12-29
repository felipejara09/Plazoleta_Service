package com.pragma.powerup.domain.exception;

public class DishNotFoundException extends DomainException {
    public DishNotFoundException() {
        super("Dish not found");
    }
}
