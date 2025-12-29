package com.pragma.powerup.infrastructure.out.util;

import com.pragma.powerup.domain.spi.IPinGeneratorPort;

import java.security.SecureRandom;

public class PinGeneratorAdapter implements IPinGeneratorPort {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generatePin() {
        int value = 100000 + random.nextInt(900000);
        return String.valueOf(value);
    }
}
