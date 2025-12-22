package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderService;
import com.pragma.powerup.domain.exception.ClientHasActiveOrderException;
import com.pragma.powerup.domain.exception.DishDoesNotBelongToRestaurantException;
import com.pragma.powerup.domain.exception.InvalidOrderException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.validation.OrderBusinessValidator;
import com.pragma.powerup.domain.validation.OrderDataValidator;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.EnumSet;

@RequiredArgsConstructor
public class OrderUseCase implements IOrderService {

    private final IOrderPersistencePort orderPersistencePort;
    private final OrderDataValidator dataValidator;
    private final OrderBusinessValidator businessValidator;

    private static final EnumSet<OrderStatus> ACTIVE =
            EnumSet.of(OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO);

    @Override
    public Order createOrder(Order order) {

        dataValidator.validateForCreate(order);

        businessValidator.validateClientHasNoActiveOrder(order.getClientId());
        businessValidator.validateRestaurantExists(order.getRestaurantId());
        businessValidator.validateAllDishesBelongToRestaurantAndAreActive(order);

        order.setStatus(OrderStatus.PENDIENTE);
        order.setCreatedAt(LocalDateTime.now());

        return orderPersistencePort.save(order);
    }
}
