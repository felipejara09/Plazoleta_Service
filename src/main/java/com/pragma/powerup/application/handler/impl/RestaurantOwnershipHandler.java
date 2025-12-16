package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.handler.IRestaurantOwnershipHandler;
import com.pragma.powerup.domain.api.IRestaurantService;
import com.pragma.powerup.infrastructure.configuration.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantOwnershipHandler implements IRestaurantOwnershipHandler {

    private final IRestaurantService restaurantService;

    @Override
    public boolean checkOwnership(Long restaurantId) {
        Long ownerId = SecurityUtils.getUserId();
        return restaurantService.isRestaurantOwnedByAuthenticatedOwner(restaurantId, ownerId);
    }
}
