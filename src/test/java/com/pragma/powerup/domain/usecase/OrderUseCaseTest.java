package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.domain.spi.*;
import com.pragma.powerup.domain.validation.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrderUseCaseTest {

    @Mock private IOrderPersistencePort orderPersistencePort;
    @Mock private OrderDataValidator dataValidator;
    @Mock private OrderBusinessValidator businessValidator;
    @Mock private IEmployeeRestaurantPort employeeRestaurantPort;
    @Mock private OrderListEmployeeValidator listEmployeeValidator;
    @Mock private EmployeeRestaurantScopeValidator restaurantScopeValidator;
    @Mock private OrderCommandValidator commandValidator;
    @Mock private OrderAssignEmployeeValidator assignEmployeeValidator;
    @Mock private OrderReadyValidator orderReadyValidator;
    @Mock private IPinGeneratorPort pinGeneratorPort;
    @Mock private IUserExternalServicePort userExternalServicePort;
    @Mock private IMessagingPort messagingPort;
    @Mock private OrderDeliverValidator orderDeliverValidator;
    @Mock private OrderCancelValidator orderCancelValidator;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private Order buildOrder(Long id, Long restaurantId, Long clientId, OrderStatus status) {
        Order o = new Order();
        o.setId(id);
        o.setRestaurantId(restaurantId);
        o.setClientId(clientId);
        o.setStatus(status);
        o.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        o.setItems(List.of());
        return o;
    }


    @Test
    void createOrder_shouldValidateApplyRulesSetPendingAndSave() {

        Order input = new Order();
        input.setRestaurantId(1L);
        input.setClientId(2L);
        input.setItems(List.of());

        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order saved = orderUseCase.createOrder(input);


        verify(dataValidator).validateForCreate(input);
        verify(businessValidator).validateClientHasNoActiveOrder(2L);
        verify(businessValidator).validateRestaurantExists(1L);
        verify(businessValidator).validateAllDishesBelongToRestaurantAndAreActive(input);

        assertEquals(OrderStatus.PENDING, saved.getStatus());
        assertNotNull(saved.getCreatedAt());

        verify(orderPersistencePort).save(input);
    }

    @Test
    void createOrder_whenClientHasActiveOrder_shouldThrow() {

        Order input = new Order();
        input.setRestaurantId(1L);
        input.setClientId(2L);
        input.setItems(List.of());

        doThrow(new ClientHasActiveOrderException())
                .when(businessValidator).validateClientHasNoActiveOrder(2L);


        assertThrows(ClientHasActiveOrderException.class, () -> orderUseCase.createOrder(input));
        verify(orderPersistencePort, never()).save(any());
    }


    @Test
    void listOrdersForEmployeeByStatus_shouldResolveRestaurantAndReturnPage() {

        String token = "token";
        String status = "PENDING";
        int page = 0;
        int size = 10;

        OrderStatus parsed = OrderStatus.PENDING;

        when(listEmployeeValidator.parseAndValidateStatus(status)).thenReturn(parsed);
        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(99L);

        PageModel<Order> expected = new PageModel<>(
                List.of(buildOrder(1L, 99L, 2L, OrderStatus.PENDING)),
                0, 10, 1, 1, true, true
        );

        when(orderPersistencePort.findByRestaurantIdAndStatus(99L, parsed, page, size))
                .thenReturn(expected);


        PageModel<Order> result = orderUseCase.listOrdersForEmployeeByStatus(token, status, page, size);


        assertEquals(1, result.getContent().size());

        verify(listEmployeeValidator).parseAndValidateStatus(status);
        verify(listEmployeeValidator).validatePagination(page, size);

        verify(employeeRestaurantPort).getMyRestaurantId(token);
        verify(restaurantScopeValidator).validateAndGetRestaurantId(99L);

        verify(orderPersistencePort).findByRestaurantIdAndStatus(99L, parsed, page, size);
    }


    @Test
    void assignToOrderAndStartPreparation_shouldAssignEmployeeSetInPreparationAndSave() {

        String token = "token";
        Long employeeId = 7L;
        Long orderId = 50L;
        Long restaurantId = 99L;

        Order order = buildOrder(orderId, restaurantId, 2L, OrderStatus.PENDING);
        order.setAssignedEmployedId(null);

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(restaurantId);
        when(orderPersistencePort.findById(orderId)).thenReturn(order);
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));


        Order saved = orderUseCase.assignToOrderAndStartPreparation(token, employeeId, orderId);


        verify(commandValidator).validateEmployeeId(employeeId);
        verify(commandValidator).validateOrderId(orderId);

        verify(employeeRestaurantPort).getMyRestaurantId(token);
        verify(restaurantScopeValidator).validateAndGetRestaurantId(restaurantId);

        verify(restaurantScopeValidator).validateOrderBelongsToEmployeeRestaurant(order, restaurantId);
        verify(assignEmployeeValidator).validateOrderIsPending(order);
        verify(assignEmployeeValidator).validateNotAssignedToAnotherEmployee(order, employeeId);

        assertEquals(employeeId, saved.getAssignedEmployedId());
        assertEquals(OrderStatus.IN_PREPARATION, saved.getStatus());

        verify(orderPersistencePort).save(order);
    }

    @Test
    void assignToOrderAndStartPreparation_whenOrderNotFound_shouldThrow() {

        String token = "token";
        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(99L);
        when(orderPersistencePort.findById(50L)).thenReturn(null);


        assertThrows(OrderNotFoundException.class,
                () -> orderUseCase.assignToOrderAndStartPreparation(token, 7L, 50L));

        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void markOrderAsReadyAndNotifyClient_shouldGeneratePinSetReadySaveAndSendSms() {

        String token = "token";
        Long employeeId = 7L;
        Long orderId = 50L;
        Long restaurantId = 99L;

        Order order = buildOrder(orderId, restaurantId, 2L, OrderStatus.IN_PREPARATION);
        order.setAssignedEmployedId(employeeId);

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(restaurantId);
        when(orderPersistencePort.findById(orderId)).thenReturn(order);

        when(pinGeneratorPort.generatePin()).thenReturn("123456");
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userExternalServicePort.getClientPhoneNumber(token, 2L)).thenReturn("+573001112233");


        Order saved = orderUseCase.markOrderAsReadyAndNotifyClient(token, employeeId, orderId);

        verify(commandValidator).validateEmployeeId(employeeId);
        verify(commandValidator).validateOrderId(orderId);

        verify(employeeRestaurantPort).getMyRestaurantId(token);
        verify(restaurantScopeValidator).validateAndGetRestaurantId(restaurantId);

        verify(restaurantScopeValidator).validateOrderBelongsToEmployeeRestaurant(order, restaurantId);
        verify(orderReadyValidator).validate(order, employeeId);

        assertEquals(OrderStatus.READY, saved.getStatus());
        assertEquals("123456", saved.getSecurityPin());

        verify(orderPersistencePort).save(order);
        verify(userExternalServicePort).getClientPhoneNumber(token, 2L);

        verify(messagingPort).sendOrderReadySms(
                eq(token),
                eq("+573001112233"),
                contains("Security PIN: 123456")
        );
    }

    @Test
    void markOrderAsReadyAndNotifyClient_whenPhoneBlank_shouldThrowClientPhoneNotFound() {

        String token = "token";
        Long employeeId = 7L;
        Long orderId = 50L;
        Long restaurantId = 99L;

        Order order = buildOrder(orderId, restaurantId, 2L, OrderStatus.IN_PREPARATION);
        order.setAssignedEmployedId(employeeId);

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(restaurantId);
        when(orderPersistencePort.findById(orderId)).thenReturn(order);
        when(pinGeneratorPort.generatePin()).thenReturn("123456");
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userExternalServicePort.getClientPhoneNumber(token, 2L)).thenReturn(" ");


        assertThrows(ClientPhoneNotFoundException.class,
                () -> orderUseCase.markOrderAsReadyAndNotifyClient(token, employeeId, orderId));

        verify(messagingPort, never()).sendOrderReadySms(any(), any(), any());
    }

    @Test
    void markOrderAsReadyAndNotifyClient_whenMessagingFails_shouldThrowSmsNotificationFailed() {

        String token = "token";
        Long employeeId = 7L;
        Long orderId = 50L;
        Long restaurantId = 99L;

        Order order = buildOrder(orderId, restaurantId, 2L, OrderStatus.IN_PREPARATION);
        order.setAssignedEmployedId(employeeId);

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(restaurantId);
        when(orderPersistencePort.findById(orderId)).thenReturn(order);
        when(pinGeneratorPort.generatePin()).thenReturn("123456");
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userExternalServicePort.getClientPhoneNumber(token, 2L)).thenReturn("+573001112233");

        doThrow(new RuntimeException("sms down"))
                .when(messagingPort).sendOrderReadySms(any(), any(), any());


        assertThrows(SmsNotificationFailedException.class,
                () -> orderUseCase.markOrderAsReadyAndNotifyClient(token, employeeId, orderId));
    }


    @Test
    void deliverOrder_shouldValidateSetDeliveredAndSave() {

        String token = "token";
        Long employeeId = 7L;
        Long orderId = 50L;
        Long restaurantId = 99L;

        Order order = buildOrder(orderId, restaurantId, 2L, OrderStatus.READY);
        order.setAssignedEmployedId(employeeId);
        order.setSecurityPin("123456");

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(restaurantId);
        when(orderPersistencePort.findById(orderId)).thenReturn(order);
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));


        Order saved = orderUseCase.deliverOrder(token, employeeId, orderId, "123456");


        verify(commandValidator).validateEmployeeId(employeeId);
        verify(commandValidator).validateOrderId(orderId);

        verify(employeeRestaurantPort).getMyRestaurantId(token);
        verify(restaurantScopeValidator).validateAndGetRestaurantId(restaurantId);

        verify(restaurantScopeValidator).validateOrderBelongsToEmployeeRestaurant(order, restaurantId);

        verify(orderDeliverValidator).validateAssignedEmployee(order, employeeId);
        verify(orderDeliverValidator).validateOrderCanBeDelivered(order);
        verify(orderDeliverValidator).validatePin("123456", "123456");

        assertEquals(OrderStatus.DELIVERED, saved.getStatus());
        verify(orderPersistencePort).save(order);
    }

    @Test
    void deliverOrder_whenOrderNotFound_shouldThrow() {

        String token = "token";
        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(99L);
        when(orderPersistencePort.findById(50L)).thenReturn(null);


        assertThrows(OrderNotFoundException.class,
                () -> orderUseCase.deliverOrder(token, 7L, 50L, "123456"));

        verify(orderPersistencePort, never()).save(any());
    }


    @Test
    void cancelOrder_whenPendingAndOwned_shouldCancelAndSave() {

        Long clientId = 10L;
        Long orderId = 50L;

        Order order = buildOrder(orderId, 99L, clientId, OrderStatus.PENDING);

        when(orderPersistencePort.findById(orderId)).thenReturn(order);
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));


        Order result = orderUseCase.cancelOrder(clientId, orderId);


        verify(commandValidator).validateEmployeeId(clientId);
        verify(commandValidator).validateOrderId(orderId);

        verify(orderCancelValidator).validateOwnership(order, clientId);
        verify(orderCancelValidator).validateCanCancel(order);

        assertEquals(OrderStatus.CANCELED, result.getStatus());
        verify(orderPersistencePort).save(order);
    }

    @Test
    void cancelOrder_whenOrderNotFound_shouldThrow() {

        when(orderPersistencePort.findById(50L)).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> orderUseCase.cancelOrder(10L, 50L));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void cancelOrder_whenNotPending_shouldThrowAndNotSave() {

        Long clientId = 10L;
        Long orderId = 50L;

        Order order = buildOrder(orderId, 99L, clientId, OrderStatus.IN_PREPARATION);
        when(orderPersistencePort.findById(orderId)).thenReturn(order);

        doThrow(new OrderCannotBeCanceledException())
                .when(orderCancelValidator).validateCanCancel(order);


        assertThrows(OrderCannotBeCanceledException.class, () -> orderUseCase.cancelOrder(clientId, orderId));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void cancelOrder_whenNotOwned_shouldThrowAndNotSave() {

        Long clientId = 10L;
        Long orderId = 50L;

        Order order = buildOrder(orderId, 99L, 999L, OrderStatus.PENDING);
        when(orderPersistencePort.findById(orderId)).thenReturn(order);

        doThrow(new ForbiddenOrderAccessException())
                .when(orderCancelValidator).validateOwnership(order, clientId);


        assertThrows(ForbiddenOrderAccessException.class, () -> orderUseCase.cancelOrder(clientId, orderId));
        verify(orderPersistencePort, never()).save(any());
    }
}
