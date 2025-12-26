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
import com.pragma.powerup.domain.validation.*;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.EnumSet;

@RequiredArgsConstructor
public class OrderUseCase implements IOrderService {

    private final IOrderPersistencePort orderPersistencePort;
    private final OrderDataValidator dataValidator;
    private final OrderBusinessValidator businessValidator;
    private final IEmployeeRestaurantPort employeeRestaurantPort;
    private final OrderListEmployeeValidator listEmployeeValidator;
    private final EmployeeRestaurantScopeValidator restaurantScopeValidator;
    private final OrderCommandValidator commandValidator;
    private final OrderAssignEmployeeValidator assignEmployeeValidator;

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


        OrderStatus orderStatus = listEmployeeValidator.parseAndValidateStatus(status);
        listEmployeeValidator.validatePagination(page, size);

        Long restaurantId = employeeRestaurantPort.getMyRestaurantId(token);
        restaurantScopeValidator.validateAndGetRestaurantId(restaurantId);

        return orderPersistencePort.findByRestaurantIdAndStatus(restaurantId, orderStatus, page, size);
    }

    @Override
    public Order assignToOrderAndStartPreparation(String token, Long employeeId, Long orderId) {

        commandValidator.validateEmployeeId(employeeId);
        commandValidator.validateOrderId(orderId);

        Long employeeRestaurantId = employeeRestaurantPort.getMyRestaurantId(token);
        restaurantScopeValidator.validateAndGetRestaurantId(employeeRestaurantId);

        Order order = orderPersistencePort.findById(orderId);
        if (order == null) throw new OrderNotFoundException();

        restaurantScopeValidator.validateOrderBelongsToEmployeeRestaurant(order, employeeRestaurantId);

        assignEmployeeValidator.validateOrderIsPending(order);
        assignEmployeeValidator.validateNotAssignedToAnotherEmployee(order, employeeId);

        order.setAssignedEmployedId(employeeId);
        order.setStatus(OrderStatus.IN_PREPARATION);

        return orderPersistencePort.save(order);
    }
}
