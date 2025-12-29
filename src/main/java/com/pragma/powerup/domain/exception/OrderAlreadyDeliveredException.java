package com.pragma.powerup.domain.exception;

public class OrderAlreadyDeliveredException extends DomainException {
    public OrderAlreadyDeliveredException() {
        super("Delivered orders cannot be modified");
    }
}
