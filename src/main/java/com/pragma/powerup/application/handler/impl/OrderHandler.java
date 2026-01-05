package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DeliverOrderRequestDto;
import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderCreatedResponseDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.application.mapper.IOrderRequestMapper;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.IOrderService;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderItem;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.domain.spi.ISecurityPort;
import com.pragma.powerup.infrastructure.configuration.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandler implements IOrderHandler {

    private final IOrderService orderService;
    private final ISecurityPort securityPort;
    private final IOrderRequestMapper requestMapper;
    private final IOrderResponseMapper responseMapper;

    @Override
    public OrderCreatedResponseDto createOrder(OrderRequestDto request) {

        Long clientId = securityPort.getAuthenticatedUserId();

        Order order = requestMapper.toOrder(request, clientId);
        Order created = orderService.createOrder(order);

        return new OrderCreatedResponseDto(
                created.getId(),
                created.getStatus().name()
        );
    }

    @Override
    public PageModel<OrderResponseDto> listOrdersForEmployeeByStatus(String token, String status, int page, int size) {
        return orderService
                .listOrdersForEmployeeByStatus(token, status, page, size)
                .map(responseMapper::toResponse);
    }

    @Override
    public OrderResponseDto assignAndStartPreparation(Long orderId) {

        String token = securityPort.getToken();
        Long employeeId = securityPort.getAuthenticatedUserId();

        return responseMapper.toResponse(
                orderService.assignToOrderAndStartPreparation(token, employeeId, orderId)
        );
    }

    @Override
    public void deliverOrder(Long orderId, DeliverOrderRequestDto dto) {
        String token = securityPort.getToken();
        Long employeeId = securityPort.getAuthenticatedUserId();
        orderService.deliverOrder(token, employeeId, orderId, dto.getPin());
    }

    @Override
    public void cancelOrder(Long orderId) {
        Long clientId = securityPort.getAuthenticatedUserId();
        orderService.cancelOrder(clientId, orderId);
    }

}
