package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderItem;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderItemEntity;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;

    @Override
    public boolean existsByClientIdAndStatusIn(Long clientId, Set<OrderStatus> statuses) {
        var statusStrings = statuses.stream().map(Enum::name).toList();
        return orderRepository.existsByClientIdAndStatusIn(clientId, statusStrings);
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setRestaurantId(order.getRestaurantId());
        entity.setClientId(order.getClientId());
        entity.setStatus(order.getStatus().name());
        entity.setCreatedAt(order.getCreatedAt());

        var items = order.getItems().stream().map(i -> {
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrder(entity);
            itemEntity.setDishId(i.getDishId());
            itemEntity.setQuantity(i.getQuantity());
            return itemEntity;
        }).toList();

        entity.setItems(items);

        OrderEntity saved = orderRepository.save(entity);

        //-
        Order result = new Order();
        result.setId(saved.getId());
        result.setRestaurantId(saved.getRestaurantId());
        result.setClientId(saved.getClientId());
        result.setStatus(OrderStatus.valueOf(saved.getStatus()));
        result.setCreatedAt(saved.getCreatedAt());
        result.setItems(saved.getItems().stream()
                .map(it -> new OrderItem(it.getDishId(), it.getQuantity()))
                .collect(Collectors.toList())
        );

        return result;
    }
}
