package com.pragma.powerup.domain.exception;

public class SmsNotificationFailedException extends DomainException {
    public SmsNotificationFailedException() {
        super("Failed to send SMS notification");
    }
}
