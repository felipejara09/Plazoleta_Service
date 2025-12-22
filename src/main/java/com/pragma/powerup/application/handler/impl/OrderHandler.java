package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderCreatedResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.domain.api.IOrderService;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderItem;
import com.pragma.powerup.domain.spi.ISecurityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandler implements IOrderHandler {

    private final IOrderService orderService;
    private final ISecurityPort securityPort;

    @Override
    public OrderCreatedResponseDto createOrder(OrderRequestDto request) {

        Long clientId = securityPort.getAuthenticatedUserId();

        Order order = new Order();
        order.setRestaurantId(request.getRestaurantId());
        order.setClientId(clientId);
        order.setItems(
                request.getItems().stream()
                        .map(i -> new OrderItem(i.getDishId(), i.getQuantity()))
                        .toList()
        );

        Order created = orderService.createOrder(order);

        return new OrderCreatedResponseDto(created.getId(), created.getStatus().name());
    }
}
