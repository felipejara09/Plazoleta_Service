package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantService;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.validation.RestaurantBusinessValidator;
import com.pragma.powerup.domain.validation.RestaurantDataValidator;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class RestaurantUseCase implements IRestaurantService {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final RestaurantDataValidator dataValidator;
    private final RestaurantBusinessValidator businessValidator;


    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        dataValidator.validate(restaurant);
        businessValidator.validate(restaurant);
        return restaurantPersistencePort.save(restaurant);
    }

    @Override
    public boolean isRestaurantOwnedByAuthenticatedOwner(Long restaurantId, Long ownerId) {
        Restaurant restaurant = restaurantPersistencePort.findById(restaurantId)
                .orElseThrow(RestaurantNotFoundException::new);

        return restaurant.getOwnerId() != null && restaurant.getOwnerId().equals(ownerId);
    }

    @Override
    public PageModel<Restaurant> listRestaurants(int page, int size) {

        if (page < 0 || size <= 0) {
            throw new InvalidPaginationException();
        }
        return restaurantPersistencePort.findAllOrderByNameAsc(page, size);
    }
}