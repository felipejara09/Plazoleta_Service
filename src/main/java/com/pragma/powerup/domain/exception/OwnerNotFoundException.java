package com.pragma.powerup.domain.exception;

public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException() {
        super("Owner user not found");
    }
}