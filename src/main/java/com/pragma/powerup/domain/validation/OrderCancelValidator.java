package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.OrderCannotBeCanceledException;
import com.pragma.powerup.domain.exception.ForbiddenOrderAccessException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;

public class OrderCancelValidator {

    public void validateOwnership(Order order, Long clientId) {
        if (order.getClientId() == null || !order.getClientId().equals(clientId)) {
            throw new ForbiddenOrderAccessException();
        }
    }

    public void validateCanCancel(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderCannotBeCanceledException();
        }
    }
}
