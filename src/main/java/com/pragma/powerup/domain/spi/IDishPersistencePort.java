package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.model.PageModel;
import java.util.Optional;

public interface IDishPersistencePort {
    Dish save(Dish dish);
    Optional<Dish> findById(Long id);
    PageModel<Dish> findMenuByRestaurant(Long restaurantId, int page, int size, String category);

}
