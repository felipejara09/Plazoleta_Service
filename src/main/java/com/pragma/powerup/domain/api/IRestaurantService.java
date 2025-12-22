package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Restaurant;

import java.util.List;


public interface IRestaurantService {

    Restaurant createRestaurant(Restaurant restaurant);
    boolean isRestaurantOwnedByAuthenticatedOwner(Long restaurantId, Long ownerId);
    List<Restaurant> listRestaurants(int page, int size);

}