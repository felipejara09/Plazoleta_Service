package com.pragma.powerup.domain.exception;

public class OrderAlreadyAssignedException extends RuntimeException {
    public OrderAlreadyAssignedException() {
        super("Order is already assigned to another employee");
    }
}
