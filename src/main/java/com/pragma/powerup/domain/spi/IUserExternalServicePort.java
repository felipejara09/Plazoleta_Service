package com.pragma.powerup.domain.spi;

public interface IUserExternalServicePort {
    boolean isOwnerUser(Long userId);
}
