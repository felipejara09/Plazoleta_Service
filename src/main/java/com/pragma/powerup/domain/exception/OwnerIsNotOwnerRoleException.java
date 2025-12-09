package com.pragma.powerup.domain.exception;


public class OwnerIsNotOwnerRoleException extends RuntimeException {
    public OwnerIsNotOwnerRoleException() {
        super("User is not an OWNER");
    }
}
