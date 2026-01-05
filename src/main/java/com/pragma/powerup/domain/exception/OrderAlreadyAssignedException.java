package com.pragma.powerup.domain.exception;

public class OrderAlreadyAssignedException extends DomainException {
    public OrderAlreadyAssignedException() {
        super("Order is already assigned to another employee");
    }
}
