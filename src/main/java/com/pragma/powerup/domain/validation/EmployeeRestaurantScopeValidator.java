package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.EmployeeRestaurantNotFoundException;
import com.pragma.powerup.domain.exception.ForbiddenOrderAccessException;
import com.pragma.powerup.domain.model.Order;

public class EmployeeRestaurantScopeValidator {
    public Long validateAndGetRestaurantId(Long restaurantId) {
        if (restaurantId == null || restaurantId <= 0) {
            throw new EmployeeRestaurantNotFoundException();
        }
        return restaurantId;
    }

    public void validateOrderBelongsToEmployeeRestaurant(Order order, Long employeeRestaurantId) {
        if (!employeeRestaurantId.equals(order.getRestaurantId())) {
            throw new ForbiddenOrderAccessException();
        }
    }
}
