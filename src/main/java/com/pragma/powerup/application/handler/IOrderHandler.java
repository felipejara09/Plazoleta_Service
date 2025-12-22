package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderCreatedResponseDto;

public interface IOrderHandler {
    OrderCreatedResponseDto createOrder(OrderRequestDto request);
}
