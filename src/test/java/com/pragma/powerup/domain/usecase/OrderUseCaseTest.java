package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderItem;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.domain.spi.IEmployeeRestaurantPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock private IOrderPersistencePort orderPersistencePort;

    @Mock private OrderDataValidator dataValidator;
    @Mock private OrderBusinessValidator businessValidator;

    @Mock private IEmployeeRestaurantPort employeeRestaurantPort;

    @Mock private OrderListEmployeeValidator listEmployeeValidator;
    @Mock private EmployeeRestaurantScopeValidator restaurantScopeValidator;

    @Mock private OrderCommandValidator commandValidator;
    @Mock private OrderAssignEmployeeValidator assignEmployeeValidator;

    private OrderUseCase orderUseCase;

    private final String token = "Bearer token";
    private final Long restaurantId = 10L;
    private final Long clientId = 20L;
    private final Long employeeId = 30L;
    private final Long orderId = 40L;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCase(
                orderPersistencePort,
                dataValidator,
                businessValidator,
                employeeRestaurantPort,
                listEmployeeValidator,
                restaurantScopeValidator,
                commandValidator,
                assignEmployeeValidator
        );
    }

    private Order createValidOrderDraft() {
        Order o = new Order();
        o.setId(null);
        o.setRestaurantId(restaurantId);
        o.setClientId(clientId);
        o.setItems(List.of(new OrderItem(1L, 2)));
        o.setStatus(null);
        o.setCreatedAt(null);
        return o;
    }

    @Test
    void createOrder_success_setsPendingAndCreatedAt_andSaves() {
        Order draft = createValidOrderDraft();

        // Validaciones OK
        doNothing().when(dataValidator).validateForCreate(draft);
        doNothing().when(businessValidator).validateClientHasNoActiveOrder(clientId);
        doNothing().when(businessValidator).validateRestaurantExists(restaurantId);
        doNothing().when(businessValidator).validateAllDishesBelongToRestaurantAndAreActive(draft);

        Order saved = createValidOrderDraft();
        saved.setId(999L);
        saved.setStatus(OrderStatus.PENDING);
        saved.setCreatedAt(LocalDateTime.now());

        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> {
            Order arg = inv.getArgument(0);
            arg.setId(999L);
            return arg;
        });

        Order result = orderUseCase.createOrder(draft);

        assertNotNull(result);
        assertEquals(999L, result.getId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertNotNull(result.getCreatedAt());

        verify(dataValidator).validateForCreate(draft);
        verify(businessValidator).validateClientHasNoActiveOrder(clientId);
        verify(businessValidator).validateRestaurantExists(restaurantId);
        verify(businessValidator).validateAllDishesBelongToRestaurantAndAreActive(draft);
        verify(orderPersistencePort).save(draft);
    }

    @Test
    void createOrder_whenDataValidatorThrows_shouldNotCallPersistence() {
        Order draft = createValidOrderDraft();

        doThrow(new RuntimeException("boom")).when(dataValidator).validateForCreate(draft);

        assertThrows(RuntimeException.class, () -> orderUseCase.createOrder(draft));

        verify(dataValidator).validateForCreate(draft);
        verifyNoInteractions(businessValidator);
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void listOrdersForEmployeeByStatus_success_callsPortWithEmployeeRestaurant() {
        String status = "PENDING";
        OrderStatus parsed = OrderStatus.PENDING;

        when(listEmployeeValidator.parseAndValidateStatus(status)).thenReturn(parsed);
        doNothing().when(listEmployeeValidator).validatePagination(0, 10);

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(restaurantId);
        when(restaurantScopeValidator.validateAndGetRestaurantId(restaurantId)).thenReturn(restaurantId);

        PageModel<Order> page = new PageModel<>(
                List.of(new Order(), new Order()),
                0, 10,
                2L, 1,
                true, true
        );

        when(orderPersistencePort.findByRestaurantIdAndStatus(restaurantId, parsed, 0, 10))
                .thenReturn(page);

        PageModel<Order> result = orderUseCase.listOrdersForEmployeeByStatus(token, status, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(listEmployeeValidator).parseAndValidateStatus(status);
        verify(listEmployeeValidator).validatePagination(0, 10);
        verify(employeeRestaurantPort).getMyRestaurantId(token);
        verify(restaurantScopeValidator).validateAndGetRestaurantId(restaurantId);
        verify(orderPersistencePort).findByRestaurantIdAndStatus(restaurantId, parsed, 0, 10);
    }

    @Test
    void listOrdersForEmployeeByStatus_whenRestaurantIdNull_shouldFailBeforeQuerying() {
        String status = "PENDING";
        OrderStatus parsed = OrderStatus.PENDING;

        when(listEmployeeValidator.parseAndValidateStatus(status)).thenReturn(parsed);
        doNothing().when(listEmployeeValidator).validatePagination(0, 10);

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(null);
        // tu validator debe lanzar acá
        doThrow(new RuntimeException("Employee has no restaurant"))
                .when(restaurantScopeValidator).validateAndGetRestaurantId(null);

        assertThrows(RuntimeException.class,
                () -> orderUseCase.listOrdersForEmployeeByStatus(token, status, 0, 10));

        verify(orderPersistencePort, never()).findByRestaurantIdAndStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    void assignToOrderAndStartPreparation_success_setsEmployeeAndInPreparation_andSaves() {
        // command validation
        doNothing().when(commandValidator).validateEmployeeId(employeeId);
        doNothing().when(commandValidator).validateOrderId(orderId);

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(restaurantId);
        when(restaurantScopeValidator.validateAndGetRestaurantId(restaurantId)).thenReturn(restaurantId);

        Order existing = new Order();
        existing.setId(orderId);
        existing.setRestaurantId(restaurantId);
        existing.setClientId(clientId);
        existing.setStatus(OrderStatus.PENDING);
        existing.setAssignedEmployedId(null);

        when(orderPersistencePort.findById(orderId)).thenReturn(existing);

        doNothing().when(restaurantScopeValidator).validateOrderBelongsToEmployeeRestaurant(existing, restaurantId);

        doNothing().when(assignEmployeeValidator).validateOrderIsPending(existing);
        doNothing().when(assignEmployeeValidator).validateNotAssignedToAnotherEmployee(existing, employeeId);

        when(orderPersistencePort.save(existing)).thenReturn(existing);

        Order result = orderUseCase.assignToOrderAndStartPreparation(token, employeeId, orderId);

        assertNotNull(result);
        assertEquals(employeeId, result.getAssignedEmployedId());
        assertEquals(OrderStatus.IN_PREPARATION, result.getStatus());

        verify(commandValidator).validateEmployeeId(employeeId);
        verify(commandValidator).validateOrderId(orderId);

        verify(employeeRestaurantPort).getMyRestaurantId(token);
        verify(restaurantScopeValidator).validateAndGetRestaurantId(restaurantId);

        verify(orderPersistencePort).findById(orderId);
        verify(restaurantScopeValidator).validateOrderBelongsToEmployeeRestaurant(existing, restaurantId);

        verify(assignEmployeeValidator).validateOrderIsPending(existing);
        verify(assignEmployeeValidator).validateNotAssignedToAnotherEmployee(existing, employeeId);

        verify(orderPersistencePort).save(existing);
    }

    @Test
    void assignToOrderAndStartPreparation_whenOrderNotFound_shouldThrowOrderNotFoundException() {
        doNothing().when(commandValidator).validateEmployeeId(employeeId);
        doNothing().when(commandValidator).validateOrderId(orderId);

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(restaurantId);
        when(restaurantScopeValidator.validateAndGetRestaurantId(restaurantId)).thenReturn(restaurantId);

        when(orderPersistencePort.findById(orderId)).thenReturn(null);

        assertThrows(OrderNotFoundException.class,
                () -> orderUseCase.assignToOrderAndStartPreparation(token, employeeId, orderId));

        verify(orderPersistencePort, never()).save(any());
        verify(assignEmployeeValidator, never()).validateOrderIsPending(any());
    }

    @Test
    void assignToOrderAndStartPreparation_whenScopeValidatorFails_shouldNotSave() {
        doNothing().when(commandValidator).validateEmployeeId(employeeId);
        doNothing().when(commandValidator).validateOrderId(orderId);

        when(employeeRestaurantPort.getMyRestaurantId(token)).thenReturn(restaurantId);
        when(restaurantScopeValidator.validateAndGetRestaurantId(restaurantId)).thenReturn(restaurantId);

        Order existing = new Order();
        existing.setId(orderId);
        existing.setRestaurantId(999L);
        existing.setStatus(OrderStatus.PENDING);

        when(orderPersistencePort.findById(orderId)).thenReturn(existing);

        doThrow(new RuntimeException("Order does not belong to employee restaurant"))
                .when(restaurantScopeValidator).validateOrderBelongsToEmployeeRestaurant(existing, restaurantId);

        assertThrows(RuntimeException.class,
                () -> orderUseCase.assignToOrderAndStartPreparation(token, employeeId, orderId));

        verify(orderPersistencePort, never()).save(any());
        verify(assignEmployeeValidator, never()).validateOrderIsPending(any());
    }
}
