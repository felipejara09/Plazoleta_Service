package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.domain.model.Restaurant;



public interface IRestaurantService {

    Restaurant createRestaurant(Restaurant restaurant);
    boolean isRestaurantOwnedByAuthenticatedOwner(Long restaurantId, Long ownerId);
    PageModel<Restaurant> listRestaurants(int page, int size);

}