package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;

public class OrderDeliverValidator {
    public void validateOrderCanBeDelivered(Order order){
        if (order.getStatus() == OrderStatus.DELIVERED){
            throw new OrderAlreadyDeliveredException();
        }
        if (order.getStatus() != OrderStatus.READY){
            throw new OrderNotReadyException();
        }
    }

    public void validatePin(String expectedPin, String providedPin){
        if (providedPin == null || providedPin.isBlank()){
            throw new InvalidSecurityPinException();
        }
        if (expectedPin == null || expectedPin.isBlank()){
            throw new InvalidSecurityPinException();
        }
        if (!expectedPin.equals(providedPin.trim())){
            throw new InvalidSecurityPinException();
        }

    }

    public void validateAssignedEmployee(Order order, Long employeeId) {
        if (order.getAssignedEmployedId() == null || !order.getAssignedEmployedId().equals(employeeId)) {
            throw new OrderNotAssignedToEmployeeException();
        }
    }
}
