package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Restaurant;
import java.util.Optional;

public interface IRestaurantPersistencePort {
    Restaurant save(Restaurant restaurant);
    boolean existsByNitId(String nit);
    boolean existsById(Long id);
    Optional<Restaurant> findById(Long id);
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}