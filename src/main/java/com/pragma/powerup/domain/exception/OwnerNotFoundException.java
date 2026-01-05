package com.pragma.powerup.domain.exception;

public class OwnerNotFoundException extends DomainException {
    public OwnerNotFoundException() {
        super("Owner user not found");
    }
}