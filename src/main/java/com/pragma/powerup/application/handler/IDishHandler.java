package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;

public interface IDishHandler {
    void createDish(DishRequestDto dishRequestDto);
    void updateDish(Long dishId, DishUpdateRequestDto dishUpdateRequestDto);

}
