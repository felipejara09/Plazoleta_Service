package com.pragma.powerup.domain.exception;


public class OwnerIsNotOwnerRoleException extends DomainException {
    public OwnerIsNotOwnerRoleException() {
        super("User is not an OWNER");
    }
}
