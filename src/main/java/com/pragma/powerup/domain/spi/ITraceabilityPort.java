package com.pragma.powerup.domain.spi;

public interface ITraceabilityPort {
    void registerStatusChange(
            String token,
            Long orderId,
            Long clientId,
            Long restaurantId,
            String previousStatus,
            String newStatus,
            Long changedByUserId,
            String changedByRole
    );
}
