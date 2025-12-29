package com.pragma.powerup.domain.spi;

public interface IMessagingPort {
    void sendOrderReadySms(String token, String phoneNumber, String message);
}
