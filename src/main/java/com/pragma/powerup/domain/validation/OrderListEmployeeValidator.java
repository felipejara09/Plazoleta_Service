package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.InvalidOrderStatusFilterException;
import com.pragma.powerup.domain.exception.InvalidPaginationException;
import com.pragma.powerup.domain.model.OrderStatus;

public class OrderListEmployeeValidator {

    public OrderStatus parseAndValidateStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new InvalidOrderStatusFilterException();
        }
        try {
            return OrderStatus.valueOf(status);
        } catch (Exception e) {
            throw new InvalidOrderStatusFilterException();
        }
    }

    public void validatePagination(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new InvalidPaginationException();
        }
    }
}
