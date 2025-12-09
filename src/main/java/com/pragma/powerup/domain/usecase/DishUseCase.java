package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDishService;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.util.ValidationConstants;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class DishUseCase implements IDishService {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    @Override
    public void createDish(Dish dish) {


        if (dish.getName() == null
                || dish.getName().isBlank()
                || !Pattern.matches(ValidationConstants.NAME_REGEX, dish.getName())) {
            throw new InvalidDishNameException();
        }


        if (dish.getPrice() == null
                || dish.getPrice() < ValidationConstants.MIN_PRICE) {
            throw new InvalidDishPriceException();
        }


        if (dish.getDescription() == null || dish.getDescription().isBlank()) {
            throw new InvalidDishDescriptionException();
        }

        if (dish.getImageUrl() == null
                || !Pattern.matches(ValidationConstants.URL_REGEX, dish.getImageUrl())) {
            throw new InvalidDishImageUrlException();
        }


        if (dish.getRestaurantId() == null
                || !restaurantPersistencePort.existsById(dish.getRestaurantId())) {
            throw new RestaurantNotFoundException();
        }


        if (Objects.isNull(dish.getActive())) {
            dish.setActive(true);
        }

        dishPersistencePort.save(dish);
    }

    @Override
    public void updateDish(Long dishId, Integer price, String description) {
        Dish existingDish = dishPersistencePort.findById(dishId)
                .orElseThrow(DishNotFoundException::new);

        if (price == null || price < ValidationConstants.MIN_PRICE) {
            throw new InvalidDishPriceException();
        }

        if (description == null || description.isBlank()) {
            throw new InvalidDishDescriptionException();
        }

        existingDish.setPrice(price);
        existingDish.setDescription(description);

        dishPersistencePort.save(existingDish);
    }
}



