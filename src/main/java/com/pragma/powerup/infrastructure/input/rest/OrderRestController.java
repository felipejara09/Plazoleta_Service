package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderCreatedResponseDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.domain.api.IOrderService;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.infrastructure.configuration.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderRestController {

    private final IOrderHandler orderHandler;
    private final IOrderService orderService;


    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/client/orders")
    public ResponseEntity<OrderCreatedResponseDto> createOrder(
            @Validated @RequestBody OrderRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderHandler.createOrder(request));
    }

    @PreAuthorize("hasRole('EMPLOYED')")
    @GetMapping("employee/orders")
    public PageModel<OrderResponseDto> listByStatus(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String status,
            @RequestParam int page,
            @RequestParam int size
    ) {
        String token = authorization.replace("Bearer ", "").trim();
        return orderHandler.listOrdersForEmployeeByStatus(token, status, page, size);
    }

    @PreAuthorize("hasRole('EMPLOYED')")
    @PatchMapping("/employee/orders/{orderId}/assign")
    public ResponseEntity<OrderResponseDto> assignAndStart(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderHandler.assignAndStartPreparation(orderId));
    }


    @PreAuthorize("hasRole('EMPLOYED')")
    @PatchMapping("/employee/orders/{orderId}/ready")
    public ResponseEntity<Void> markReady(@PathVariable Long orderId) {

        String token = SecurityUtils.getToken();
        Long employeeId = SecurityUtils.getUserId();

        orderService.markOrderAsReadyAndNotifyClient(token, employeeId, orderId);

        return ResponseEntity.ok().build();
    }


}


