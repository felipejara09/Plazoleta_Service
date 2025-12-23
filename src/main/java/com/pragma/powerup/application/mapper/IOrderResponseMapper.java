package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.OrderItemResponseDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IOrderResponseMapper {
    OrderResponseDto toResponse(Order order);
    OrderItemResponseDto toItemResponse(OrderItem item);
}
