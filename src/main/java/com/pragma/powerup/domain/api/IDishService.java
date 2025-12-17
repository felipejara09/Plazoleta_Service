package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Dish;

import java.util.List;

public interface IDishService {

    void createDish(Dish dish);

    void updateDish(Long dishId, Integer price, String a);

    void changeDishStatus(Long dishId, Boolean active, Long ownerId);
    List<Dish> listMenu(Long restaurantId, int page, int size, String category);

}
