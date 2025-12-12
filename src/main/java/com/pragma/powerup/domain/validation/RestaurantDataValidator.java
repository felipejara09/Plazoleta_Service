package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.InvalidLogoUrlException;
import com.pragma.powerup.domain.exception.InvalidRestaurantNameException;
import com.pragma.powerup.domain.exception.InvalidRestaurantNitException;
import com.pragma.powerup.domain.exception.InvalidRestaurantPhoneException;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.util.ValidationConstants;

import java.util.regex.Pattern;

import static com.pragma.powerup.domain.util.ValidationConstants.URL_REGEX;

public class RestaurantDataValidator {

    public void validate(Restaurant restaurant) {
        if (restaurant.getName() == null ||
                restaurant.getName().trim().isEmpty() ||
                restaurant.getName().trim().matches(ValidationConstants.ONLY_NUMBER_REGEX)) {
            throw new InvalidRestaurantNameException();
        }

        if (restaurant.getNitId() == null ||
                !Pattern.matches(ValidationConstants.ONLY_NUMBER_REGEX, restaurant.getNitId())) {
            throw new InvalidRestaurantNitException();
        }

        if (restaurant.getPhoneNumber() == null ||
                !Pattern.matches(ValidationConstants.PHONE_REGEX, restaurant.getPhoneNumber())) {
            throw new InvalidRestaurantPhoneException();
        }

        if (restaurant.getLogoUrl() == null ||
                !restaurant.getLogoUrl().matches(URL_REGEX)) {
            throw new InvalidLogoUrlException();
        }
    }
}
