package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantListResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;

import java.util.List;

public interface IRestaurantHandler {
    RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto);
    List<RestaurantListResponseDto> listRestaurants(int page, int size);

}