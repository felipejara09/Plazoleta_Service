package com.pragma.powerup.domain.validation;

import com.pragma.powerup.domain.exception.ClientHasActiveOrderException;
import com.pragma.powerup.domain.exception.DishDoesNotBelongToRestaurantException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;

@RequiredArgsConstructor
public class OrderBusinessValidator {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;

    private static final EnumSet<OrderStatus> ACTIVE =
            EnumSet.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION, OrderStatus.READY);

    public void validateClientHasNoActiveOrder(Long clientId) {
        boolean hasActive = orderPersistencePort.existsByClientIdAndStatusIn(clientId, ACTIVE);
        if (hasActive) throw new ClientHasActiveOrderException();
    }

    public void validateRestaurantExists(Long restaurantId) {
        if (!restaurantPersistencePort.existsById(restaurantId)) {
            throw new RestaurantNotFoundException();
        }
    }

    public void validateAllDishesBelongToRestaurantAndAreActive(Order order) {
        Long restaurantId = order.getRestaurantId();

        boolean invalidDishFound = order.getItems().stream().anyMatch(item ->
                !dishPersistencePort.existsByIdAndRestaurantIdAndActiveTrue(item.getDishId(), restaurantId)
        );

        if (invalidDishFound) {
            throw new DishDoesNotBelongToRestaurantException();
        }
    }
}
