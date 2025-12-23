package com.pragma.powerup.domain.exception;

public class InvalidOrderStatusFilterException extends DomainException {
    public InvalidOrderStatusFilterException() {
        super("Order status filter is required");
    }
}
