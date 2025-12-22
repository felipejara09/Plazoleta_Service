package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDishService;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.validation.DishBusinessValidator;
import com.pragma.powerup.domain.validation.DishDataValidator;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class DishUseCase implements IDishService {

    private final IDishPersistencePort dishPersistencePort;
    private final DishDataValidator dataValidator;
    private final DishBusinessValidator businessValidator;

    @Override
    public void createDish(Dish dish) {
        dataValidator.validateForCreate(dish);
        businessValidator.validateRestaurantExists(dish);
        dish.setActive(true);
        dishPersistencePort.save(dish);
    }

    @Override
    public void updateDish(Long dishId, Integer price, String description) {
        Dish existingDish = dishPersistencePort.findById(dishId)
                .orElseThrow(DishNotFoundException::new);

        dataValidator.validateForUpdate(price, description);
        existingDish.setPrice(price);
        existingDish.setDescription(description);
        dishPersistencePort.save(existingDish);
    }

    @Override
    public void changeDishStatus(Long dishId, Boolean active, Long ownerId) {
        Dish dish = dishPersistencePort.findById(dishId)
                .orElseThrow(DishNotFoundException::new);

        businessValidator.validateRestaurantBelongsToOwner(dish.getRestaurantId(), ownerId);

        dish.setActive(active);
        dishPersistencePort.save(dish);
    }
}



