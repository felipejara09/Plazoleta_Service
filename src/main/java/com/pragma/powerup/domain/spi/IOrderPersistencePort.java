package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;

import java.util.Set;

public interface IOrderPersistencePort {
    boolean existsByClientIdAndStatusIn(Long clientId, Set<OrderStatus> statuses);
    Order save(Order order);
}
