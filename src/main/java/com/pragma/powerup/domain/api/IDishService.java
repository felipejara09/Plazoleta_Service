package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Dish;

public interface IDishService {

    void createDish(Dish dish);
    void updateDish(Long dishId, Integer price, String description);
}
