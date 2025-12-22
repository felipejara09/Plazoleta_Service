package com.pragma.powerup.domain.exception;

public class ClientHasActiveOrderException extends DomainException {
    public ClientHasActiveOrderException() {
        super("Client already has an active order");
    }
}
