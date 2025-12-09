package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Restaurant;
import java.util.List;

public interface IRestaurantPersistencePort {
    Restaurant save(Restaurant restaurant);
    boolean existsByNitId(String nit);
    boolean existsById(Long id);
}