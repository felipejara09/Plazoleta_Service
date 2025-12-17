package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.dto.response.DishMenuResponseDto;

import java.util.List;

public interface IDishHandler {
    void createDish(DishRequestDto dishRequestDto);
    void updateDish(Long dishId, DishUpdateRequestDto dishUpdateRequestDto);
    void changeDishStatus(Long dishId, Boolean active);
    List<DishMenuResponseDto> listMenu(Long restaurantId, int page, int size, String category);

}
