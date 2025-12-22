package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import org.springframework.stereotype.Component;

@Component
public class UserExternalServiceAdapter implements IUserExternalServicePort {

    @Override
    public boolean isOwnerUser(Long userId) {
       return true;
    }
}
