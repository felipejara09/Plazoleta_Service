package com.pragma.powerup.domain.exception;

public class RestaurantOwnershipException extends RuntimeException {
    public RestaurantOwnershipException() {
        super("The authenticated user is not the owner of the restaurant");
    }
}
