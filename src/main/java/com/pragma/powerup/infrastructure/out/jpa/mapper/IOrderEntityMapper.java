package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderItem;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderItemEntity;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface IOrderEntityMapper {


    Order toModel(OrderEntity entity);

    OrderItem toModel(OrderItemEntity entity);


    @Mapping(target = "status", source = "status")
    @Mapping(target = "items", source = "items")
    OrderEntity toEntity(Order model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItemEntity toEntity(OrderItem model);


    default OrderStatus map(String status) {
        return OrderStatus.valueOf(status);
    }

    default String map(OrderStatus status) {
        return status.name();
    }


    @AfterMapping
    default void linkItems(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity.getItems() != null) {
            orderEntity.getItems().forEach(i -> i.setOrder(orderEntity));
        }
    }

    default PageModel<Order> toPageModel(Page<OrderEntity> page) {
        return new PageModel<>(
                page.getContent().stream().map(this::toModel).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}


