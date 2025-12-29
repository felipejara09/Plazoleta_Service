package com.pragma.powerup.domain.exception;

public class OrderNotReadyException extends DomainException {
    public OrderNotReadyException() {
        super("Order must be in READY status to be delivered");
    }
}
