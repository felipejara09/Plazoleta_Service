package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderService;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.domain.spi.*;
import com.pragma.powerup.domain.util.RoleConstants;
import com.pragma.powerup.domain.util.ValidationConstants;
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
    private final OrderReadyValidator orderReadyValidator;
    private final IPinGeneratorPort pinGeneratorPort;
    private final IUserExternalServicePort userExternalServicePort;
    private final IMessagingPort messagingPort;
    private final OrderDeliverValidator orderDeliverValidator;
    private final OrderCancelValidator orderCancelValidator;
    private final ITraceabilityPort traceabilityPort;


    @Override
    public Order createOrder(Order order) {

        dataValidator.validateForCreate(order);

        businessValidator.validateClientHasNoActiveOrder(order.getClientId());
        businessValidator.validateRestaurantExists(order.getRestaurantId());
        businessValidator.validateAllDishesBelongToRestaurantAndAreActive(order);

        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderPersistencePort.save(order);

        traceabilityPort.registerStatusChange(
                null,
                saved.getId(),
                saved.getClientId(),
                saved.getRestaurantId(),
                ValidationConstants.STATUS_INIT,
                OrderStatus.PENDING.name(),
                saved.getClientId(),
                RoleConstants.ROLE_CLIENT
        );

        return saved;
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

        String previous = order.getStatus().name();

        order.setAssignedEmployedId(employeeId);
        order.setStatus(OrderStatus.IN_PREPARATION);

        Order saved = orderPersistencePort.save(order);

        traceabilityPort.registerStatusChange(
                token,
                saved.getId(),
                saved.getClientId(),
                saved.getRestaurantId(),
                previous,
                OrderStatus.IN_PREPARATION.name(),
                employeeId,
                RoleConstants.ROLE_EMPLOYED
        );

        return saved;
    }

    @Override
    public Order markOrderAsReadyAndNotifyClient(String token, Long employedId, Long orderId){

        commandValidator.validateEmployeeId(employedId);
        commandValidator.validateOrderId(orderId);

        Long employedRestaurantId = employeeRestaurantPort.getMyRestaurantId(token);
        restaurantScopeValidator.validateAndGetRestaurantId(employedRestaurantId);

        Order order = orderPersistencePort.findById(orderId);
        if(order == null) throw new OrderNotFoundException();

        restaurantScopeValidator.validateOrderBelongsToEmployeeRestaurant(order, employedRestaurantId);


        orderReadyValidator.validate(order, employedId);

        String previous = order.getStatus().name();

        String pin = pinGeneratorPort.generatePin();
        order.setSecurityPin(pin);
        order.setStatus(OrderStatus.READY);

        Order saved = orderPersistencePort.save(order);

        traceabilityPort.registerStatusChange(
                token,
                saved.getId(),
                saved.getClientId(),
                saved.getRestaurantId(),
                previous,
                OrderStatus.READY.name(),
                employedId,
                RoleConstants.ROLE_EMPLOYED
        );

        String phone = userExternalServicePort.getClientPhoneNumber(token, saved.getClientId());
        if (phone == null || phone.isBlank()) throw new ClientPhoneNotFoundException();


        String msg = "Your order is READY. Security PIN: " + pin;
        try {
            messagingPort.sendOrderReadySms(token, phone, msg);
        } catch (Exception e) {
            throw new SmsNotificationFailedException();
        }

        return saved;

    }

    @Override
    public Order deliverOrder(String token, Long employeeId, Long orderId, String pin) {

        commandValidator.validateEmployeeId(employeeId);
        commandValidator.validateOrderId(orderId);

        Long employeeRestaurantId = employeeRestaurantPort.getMyRestaurantId(token);
        restaurantScopeValidator.validateAndGetRestaurantId(employeeRestaurantId);

        Order order = orderPersistencePort.findById(orderId);
        if (order == null) throw new OrderNotFoundException();

        restaurantScopeValidator.validateOrderBelongsToEmployeeRestaurant(order, employeeRestaurantId);


        orderDeliverValidator.validateAssignedEmployee(order, employeeId);


        orderDeliverValidator.validateOrderCanBeDelivered(order);
        orderDeliverValidator.validatePin(order.getSecurityPin(), pin);

        String previous = order.getStatus().name();

        order.setStatus(OrderStatus.DELIVERED);

        Order saved = orderPersistencePort.save(order);

        traceabilityPort.registerStatusChange(
                token,
                saved.getId(),
                saved.getClientId(),
                saved.getRestaurantId(),
                previous,
                OrderStatus.DELIVERED.name(),
                employeeId,
                RoleConstants.ROLE_EMPLOYED
        );

        return saved;
    }

    @Override
    public Order cancelOrder(Long clientId, Long orderId) {

        commandValidator.validateEmployeeId(clientId);
        commandValidator.validateOrderId(orderId);

        Order order = orderPersistencePort.findById(orderId);
        if (order == null) throw new OrderNotFoundException();

        orderCancelValidator.validateOwnership(order, clientId);
        orderCancelValidator.validateCanCancel(order);

        String previous = order.getStatus().name();

        order.setStatus(OrderStatus.CANCELED);

        Order saved = orderPersistencePort.save(order);


        traceabilityPort.registerStatusChange(
                null,
                saved.getId(),
                saved.getClientId(),
                saved.getRestaurantId(),
                previous,
                OrderStatus.CANCELED.name(),
                clientId,
                RoleConstants.ROLE_CLIENT
        );

        return saved;
    }




}
