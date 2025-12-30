package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderService;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.domain.spi.*;
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


        String pin = pinGeneratorPort.generatePin();
        order.setSecurityPin(pin);
        order.setStatus(OrderStatus.READY);

        Order save = orderPersistencePort.save(order);


        String phone = userExternalServicePort.getClientPhoneNumber(token, save.getClientId());
        if (phone == null || phone.isBlank()) throw new ClientPhoneNotFoundException();


        String msg = "Your order is READY. Security PIN: " + pin;
        try {
            messagingPort.sendOrderReadySms(token, phone, msg);
        } catch (Exception e) {
            throw new SmsNotificationFailedException();
        }

        return save;

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

        order.setStatus(OrderStatus.DELIVERED);

        return orderPersistencePort.save(order);
    }

    @Override
    public Order cancelOrder(Long clientId, Long orderId) {

        commandValidator.validateEmployeeId(clientId);
        commandValidator.validateOrderId(orderId);

        Order order = orderPersistencePort.findById(orderId);
        if (order == null) throw new OrderNotFoundException();

        orderCancelValidator.validateOwnership(order, clientId);
        orderCancelValidator.validateCanCancel(order);

        order.setStatus(OrderStatus.CANCELED);
        return orderPersistencePort.save(order);
    }




}
