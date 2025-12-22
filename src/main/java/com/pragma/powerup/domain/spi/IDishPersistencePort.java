package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.model.OrderStatus;

import java.util.Optional;
import java.util.Set;

public interface IDishPersistencePort {
    Dish save(Dish dish);
    Optional<Dish> findById(Long id);
    boolean existsByIdAndRestaurantIdAndActiveTrue(Long dishId, Long restaurantId);


}
