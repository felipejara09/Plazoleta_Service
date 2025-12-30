package com.pragma.powerup.domain.exception;

public class OrderCannotBeCanceledException extends DomainException {
    public OrderCannotBeCanceledException() {
        super("Sorry, your order is already in preparation and cannot be canceled");
    }
}
