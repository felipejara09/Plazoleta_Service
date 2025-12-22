package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DishBusinessValidator {
    private final IRestaurantPersistencePort restaurantPersistencePort;

    public void validateRestaurantExists(Dish dish) {
        if (dish.getRestaurantId() == null
                || !restaurantPersistencePort.existsById(dish.getRestaurantId())) {
            throw new RestaurantNotFoundException();
        }
    }
}
