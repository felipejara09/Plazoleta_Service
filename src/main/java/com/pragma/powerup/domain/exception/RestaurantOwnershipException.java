package com.pragma.powerup.domain.exception;

public class RestaurantOwnershipException extends DomainException {
    public RestaurantOwnershipException() {
        super("The authenticated user is not the owner of the restaurant");
    }
}
