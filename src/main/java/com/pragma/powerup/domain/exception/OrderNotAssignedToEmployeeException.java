package com.pragma.powerup.domain.exception;

public class OrderNotAssignedToEmployeeException extends DomainException {
    public OrderNotAssignedToEmployeeException() {
        super("Order is not assigned to this employee");
    }
}
