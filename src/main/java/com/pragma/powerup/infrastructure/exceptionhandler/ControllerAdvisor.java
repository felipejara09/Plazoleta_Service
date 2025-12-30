package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        Map<String, Object> body = new java.util.HashMap<>();
        body.put("message", "Validation failed");
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({
            InvalidRestaurantNameException.class,
            InvalidRestaurantNitException.class,
            InvalidRestaurantPhoneException.class,
            InvalidLogoUrlException.class,
            OwnerNotFoundException.class,
            OwnerIsNotOwnerRoleException.class,
            InvalidDishPriceException.class,
            InvalidDishDescriptionException.class,
            InvalidDishImageUrlException.class,
            InvalidDishNameException.class,
            InvalidOrderException.class,
            InvalidOrderStatusFilterException.class,
            InvalidPaginationException.class,
            OrderCannotBeAssignedException.class,
            OrderAlreadyAssignedException.class,
            InvalidSecurityPinException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.toString()));
    }

    @ExceptionHandler({
            DishNotFoundException.class,
            RestaurantNotFoundException.class,
            OrderNotFoundException.class,
            EmployeeRestaurantNotFoundException.class
    })
    public ResponseEntity<ExceptionResponse> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND.toString()));
    }

    @ExceptionHandler({
            ForbiddenOrderAccessException.class,
            RestaurantOwnershipException.class
    })
    public ResponseEntity<ExceptionResponse> handleForbidden(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.FORBIDDEN.toString()));
    }

    @ExceptionHandler({
            ClientHasActiveOrderException.class,
            OrderAlreadyDeliveredException.class
    })
    public ResponseEntity<ExceptionResponse> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(ex.getMessage(), HttpStatus.CONFLICT.toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR.toString()));
    }

    @ExceptionHandler(OrderCannotBeCanceledException.class)
    public ResponseEntity<ExceptionResponse> handleCannotCancel(OrderCannotBeCanceledException ex) {
        ExceptionResponse response = new ExceptionResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.toString()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
