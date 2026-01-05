package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.OrderItemRequestDto;
import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderItem;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IOrderRequestMapper {

    Order toOrder(OrderRequestDto dto, @Context Long clientId);

    default OrderItem toItem(OrderItemRequestDto dto) {
        return new OrderItem(dto.getDishId(), dto.getQuantity());
    }
}

