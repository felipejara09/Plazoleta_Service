package com.pragma.powerup.domain.exception;

public class DishNotFoundException extends RuntimeException {
    public DishNotFoundException() {
        super("Dish not found");
    }
}
