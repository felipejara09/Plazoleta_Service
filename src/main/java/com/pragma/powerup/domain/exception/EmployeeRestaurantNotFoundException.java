package com.pragma.powerup.domain.exception;

public class EmployeeRestaurantNotFoundException extends DomainException {
    public EmployeeRestaurantNotFoundException() {
        super("Employee restaurant was not found");
    }
}
