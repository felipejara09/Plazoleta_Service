package com.pragma.powerup.domain.validation;

public class OrderCommandValidator {

    public void validateEmployeeId(Long employeeId) {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employeeId");
        }
    }

    public void validateOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Invalid orderId");
        }
    }
}
