package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderService;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IEmployeeRestaurantPort;
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
    private final IEmployeeRestaurantPort employeeRestaurantPort;

    private static final EnumSet<OrderStatus> ACTIVE =
            EnumSet.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION, OrderStatus.READY);

    @Override
    public Order createOrder(Order order) {

        dataValidator.validateForCreate(order);

        businessValidator.validateClientHasNoActiveOrder(order.getClientId());
        businessValidator.validateRestaurantExists(order.getRestaurantId());
        businessValidator.validateAllDishesBelongToRestaurantAndAreActive(order);

        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        return orderPersistencePort.save(order);
    }

    @Override
    public PageModel<Order> listOrdersForEmployeeByStatus(String token, String status, int page, int size) {

        if (status == null) {
            throw new InvalidOrderStatusFilterException();
        }
        if (page < 0 || size <= 0){
            throw new InvalidPaginationException();
        }

        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status);
        } catch (Exception e) {
            throw new InvalidOrderStatusFilterException();
        }

        Long restaurantId = employeeRestaurantPort.getMyRestaurantId(token);
        if (restaurantId == null || restaurantId <= 0) throw new EmployeeRestaurantNotFoundException();

        return orderPersistencePort.findByRestaurantIdAndStatus(restaurantId, orderStatus, page, size);
    }
}
