package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.InvalidDishDescriptionException;
import com.pragma.powerup.domain.exception.InvalidDishImageUrlException;
import com.pragma.powerup.domain.exception.InvalidDishNameException;
import com.pragma.powerup.domain.exception.InvalidDishPriceException;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.util.ValidationConstants;

import java.util.regex.Pattern;

public class DishDataValidator {

    public void validateForCreate(Dish dish) {

        if (dish.getName() == null
                || dish.getName().isBlank()
                || !Pattern.matches(ValidationConstants.NAME_REGEX, dish.getName())) {
            throw new InvalidDishNameException();
        }

        if (dish.getPrice() == null || dish.getPrice() < ValidationConstants.MIN_PRICE) {
            throw new InvalidDishPriceException();
        }

        if (dish.getDescription() == null || dish.getDescription().isBlank()) {
            throw new InvalidDishDescriptionException();
        }

        if (dish.getImageUrl() == null
                || !Pattern.matches(ValidationConstants.URL_REGEX, dish.getImageUrl())) {
            throw new InvalidDishImageUrlException();
        }
    }

    public void validateForUpdate(Integer price, String description) {
        if (price == null || price < ValidationConstants.MIN_PRICE) {
            throw new InvalidDishPriceException();
        }
        if (description == null || description.isBlank()) {
            throw new InvalidDishDescriptionException();
        }
    }
}
