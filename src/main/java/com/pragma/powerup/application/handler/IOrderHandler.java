package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.DeliverOrderRequestDto;
import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderCreatedResponseDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;

public interface IOrderHandler {
    OrderCreatedResponseDto createOrder(OrderRequestDto request);
    PageModel<OrderResponseDto> listOrdersForEmployeeByStatus(String token, String status, int page, int size);
    OrderResponseDto assignAndStartPreparation(Long orderId);
    void deliverOrder(Long orderId, DeliverOrderRequestDto dto);
    void cancelOrder(Long orderId);

}
