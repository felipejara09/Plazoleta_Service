package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DeliverOrderRequestDto;
import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderCreatedResponseDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.domain.api.IOrderService;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.infrastructure.configuration.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderRestController {

    private final IOrderHandler orderHandler;
    private final IOrderService orderService;

    @Operation(
            summary = "Create order",
            description = "Allows a client to create a new order for a specific restaurant."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderCreatedResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Client already has an active order or invalid order data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only clients can create orders",
                    content = @Content
            )
    })


    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/client/orders")
    public ResponseEntity<OrderCreatedResponseDto> createOrder(
            @Validated @RequestBody OrderRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderHandler.createOrder(request));
    }

    @Operation(
            summary = "List orders by status",
            description = "Allows an employee to list restaurant orders filtered by status with pagination."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageModel.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status or pagination parameters",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only employees can access this resource",
                    content = @Content
            )
    })

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


    @Operation(
            summary = "Assign order and start preparation",
            description = "Allows an employee to take ownership of an order and change its status to IN_PREPARATION."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order assigned and preparation started successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Order cannot be assigned due to its current status",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only employees can assign orders",
                    content = @Content
            )
    })

    @PreAuthorize("hasRole('EMPLOYED')")
    @PatchMapping("/employee/orders/{orderId}/assign")
    public ResponseEntity<OrderResponseDto> assignAndStart(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderHandler.assignAndStartPreparation(orderId));
    }

    @Operation(
            summary = "Mark order as ready",
            description = "Allows an employee to mark an order as READY and notify the client."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order marked as ready successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Order is not in preparation state",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only employees can mark orders as ready",
                    content = @Content
            )
    })

    @PreAuthorize("hasRole('EMPLOYED')")
    @PatchMapping("/employee/orders/{orderId}/ready")
    public ResponseEntity<Void> markReady(@PathVariable Long orderId) {

        String token = SecurityUtils.getToken();
        Long employeeId = SecurityUtils.getUserId();

        orderService.markOrderAsReadyAndNotifyClient(token, employeeId, orderId);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Deliver order",
            description = "Allows an employee to deliver an order after validating the security PIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order delivered successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid security PIN or order cannot be delivered",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only employees can deliver orders",
                    content = @Content
            )
    })

    @PreAuthorize("hasRole('EMPLOYED')")
    @PatchMapping("/employee/orders/{orderId}/deliver")
    public ResponseEntity<Void> deliverOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody DeliverOrderRequestDto dto
    ) {
        orderHandler.deliverOrder(orderId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Cancel order",
            description = "Allows a client to cancel an order if it has not started preparation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order canceled successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Order cannot be canceled due to its current status",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only clients can cancel orders",
                    content = @Content
            )
    })

    @PreAuthorize("hasRole('CLIENT')")
    @PatchMapping("/client/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId) {
        orderHandler.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }



}


