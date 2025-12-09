package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler({
            InvalidRestaurantNameException.class,
            InvalidRestaurantNitException.class,
            InvalidRestaurantPhoneException.class,
            InvalidLogoUrlException.class,
            OwnerNotFoundException.class,
            OwnerIsNotOwnerRoleException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequest(RuntimeException ex) {
        ExceptionResponse response = new ExceptionResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.toString()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
}