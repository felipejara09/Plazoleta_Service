package com.pragma.powerup.domain.exception;

public class ClientPhoneNotFoundException extends DomainException {
    public ClientPhoneNotFoundException() {
        super("Client phone number was not found");
    }

}
