package com.pragma.powerup.domain.exception;

public class InvalidPaginationException extends RuntimeException {
    public InvalidPaginationException() {
        super("Invalid pagination parameters: page must be >= 0 and size must be > 0");
    }
}