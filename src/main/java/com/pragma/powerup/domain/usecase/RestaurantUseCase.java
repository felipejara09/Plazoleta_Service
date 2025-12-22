package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantService;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import com.pragma.powerup.domain.util.ValidationConstants;
import lombok.AllArgsConstructor;

import java.util.regex.Pattern;


import static com.pragma.powerup.domain.util.ValidationConstants.URL_REGEX;

@AllArgsConstructor
public class RestaurantUseCase implements IRestaurantService {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserExternalServicePort userExternalServicePort;

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {

        if (restaurant.getName() == null ||
                restaurant.getName().trim().isEmpty() ||
                restaurant.getName().trim().matches("^\\d+$")) {
            throw new InvalidRestaurantNameException();
        }
        if (!Pattern.matches(ValidationConstants.NIT_REGEX, restaurant.getNitId())) {
            throw new InvalidRestaurantNitException();
        }
        if (!Pattern.matches(ValidationConstants.PHONE_REGEX, restaurant.getPhoneNumber())) {
            throw new InvalidRestaurantPhoneException();
        }
        if (restaurantPersistencePort.existsByNitId(restaurant.getNitId())) {
            throw new InvalidRestaurantNitException();
        }
        if (restaurant.getOwnerId() == null ||
                !userExternalServicePort.isOwnerUser(restaurant.getOwnerId())) {
            throw new OwnerIsNotOwnerRoleException();
        }
        if (restaurant.getLogoUrl() == null ||
                !restaurant.getLogoUrl().matches(URL_REGEX)) {
            throw new InvalidLogoUrlException();
        }

        return restaurantPersistencePort.save(restaurant);
    }
}