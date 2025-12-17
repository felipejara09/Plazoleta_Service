package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Dish;

import java.util.List;
import java.util.Optional;

public interface IDishPersistencePort {
    Dish save(Dish dish);
    Optional<Dish> findById(Long id);
    List<Dish> findMenuByRestaurant(Long restaurantId, int page, int size, String category);

}
