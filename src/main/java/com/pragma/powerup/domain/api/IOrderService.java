package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;

public interface IOrderService {
    Order createOrder(Order order);
    PageModel<Order> listOrdersForEmployeeByStatus(String token, String status, int page, int size);
}
