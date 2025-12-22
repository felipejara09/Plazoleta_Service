package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PageResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantListResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.mapper.IRestaurantRequestMapper;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.domain.api.IRestaurantService;
import com.pragma.powerup.domain.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantService restaurantService;
    private final IRestaurantRequestMapper requestMapper;
    private final IRestaurantResponseMapper responseMapper;

    @Override
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto dto) {
        Restaurant restaurant = requestMapper.toRestaurant(dto);
        Restaurant saved = restaurantService.createRestaurant(restaurant);
        return responseMapper.toResponse(saved);
    }


    @Override
    public PageResponseDto<RestaurantListResponseDto> listRestaurants(int page, int size) {

        var mapped = restaurantService.listRestaurants(page, size)
                .map(responseMapper::toListResponse);

        return new PageResponseDto<>(
                mapped.getContent(),
                new PageResponseDto.Meta(
                        mapped.getPageNumber(),
                        mapped.getPageSize(),
                        mapped.getTotalElements(),
                        mapped.getTotalPages(),
                        mapped.isFirst(),
                        mapped.isLast(),
                        mapped.hasNext(),
                        mapped.hasPrevious()
                )
        );
    }
}