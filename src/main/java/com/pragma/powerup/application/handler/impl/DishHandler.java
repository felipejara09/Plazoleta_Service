package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.handler.IDishHandler;
import com.pragma.powerup.application.mapper.IDishRequestMapper;
import com.pragma.powerup.domain.api.IDishService;
import com.pragma.powerup.domain.spi.ISecurityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishService dishService;
    private final IDishRequestMapper dishRequestMapper;
    private final ISecurityPort securityPort;

    @Override
    public void createDish(DishRequestDto dishRequestDto) {
        dishService.createDish(dishRequestMapper.toDish(dishRequestDto));
    }

    @Override
    public void updateDish(Long dishId, DishUpdateRequestDto dishUpdateRequestDto) {
        dishService.updateDish(
                dishId,
                dishUpdateRequestDto.getPrice(),
                dishUpdateRequestDto.getDescription()
        );
    }

    @Override
    public void changeDishStatus(Long dishId, Boolean active) {
        Long ownerId = securityPort.getAuthenticatedUserId();
        dishService.changeDishStatus(dishId, active, ownerId);
    }
}
