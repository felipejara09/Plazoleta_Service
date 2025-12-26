package com.pragma.powerup.domain.exception;

public class ForbiddenOrderAccessException extends RuntimeException {
    public ForbiddenOrderAccessException() {
        super("You are not allowed to access this order");
    }
}
