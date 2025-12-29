package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.OrderIsNotInPreparationException;
import com.pragma.powerup.domain.exception.OrderNotAssignedToEmployeeException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;

public class OrderReadyValidator {
    public void validate(Order order, Long employedId){
        if (order.getStatus() != OrderStatus.IN_PREPARATION){
            throw new OrderIsNotInPreparationException();
        }

        if (order.getAssignedEmployedId() == null || !order.getAssignedEmployedId().equals(employedId)){
            throw new OrderNotAssignedToEmployeeException();
        }

    }
}
