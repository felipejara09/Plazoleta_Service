package com.pragma.powerup.domain.exception;


public class InvalidLogoUrlException extends RuntimeException {
    public InvalidLogoUrlException() {
        super("Invalid logo URL");
    }
}
