package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.OrderAlreadyAssignedException;
import com.pragma.powerup.domain.exception.OrderCannotBeAssignedException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;

public class OrderAssignEmployeeValidator {

    public void validateOrderIsPending(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderCannotBeAssignedException();
        }
    }

    public void validateNotAssignedToAnotherEmployee(Order order, Long employeeId) {
        if (order.getAssignedEmployedId() != null &&
                !order.getAssignedEmployedId().equals(employeeId)) {
            throw new OrderAlreadyAssignedException();
        }
    }
}
