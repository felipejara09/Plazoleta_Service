package com.pragma.powerup.domain.exception;

public class OrderIsNotInPreparationException extends DomainException {
    public OrderIsNotInPreparationException() {
        super("Order must be in preparation to be marked as ready");
    }
}
