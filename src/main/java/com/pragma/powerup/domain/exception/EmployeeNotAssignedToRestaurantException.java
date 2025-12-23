package com.pragma.powerup.domain.exception;

public class EmployeeNotAssignedToRestaurantException extends DomainException {
    public EmployeeNotAssignedToRestaurantException() {
        super("Employee is not assigned to any restaurant");
    }
}
