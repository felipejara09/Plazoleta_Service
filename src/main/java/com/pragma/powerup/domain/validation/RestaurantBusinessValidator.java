package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.InvalidRestaurantNitException;
import com.pragma.powerup.domain.exception.OwnerIsNotOwnerRoleException;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RestaurantBusinessValidator {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserExternalServicePort userExternalServicePort;


    public void validate(Restaurant restaurant) {
        if (restaurantPersistencePort.existsByNitId(restaurant.getNitId())) {
            throw new InvalidRestaurantNitException();
        }

        if (restaurant.getOwnerId() == null ||
                !userExternalServicePort.isOwnerUser(restaurant.getOwnerId())) {
            throw new OwnerIsNotOwnerRoleException();
        }
    }
}
