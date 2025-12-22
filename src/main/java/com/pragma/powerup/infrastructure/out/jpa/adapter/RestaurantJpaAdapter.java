package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public Restaurant save(Restaurant restaurant) {
        RestaurantEntity entity = restaurantEntityMapper.toEntity(restaurant);
        return restaurantEntityMapper.toRestaurant(restaurantRepository.save(entity));
    }

    @Override
    public boolean existsByNitId(String nit) {
        return restaurantRepository.existsByNitId(nit);
    }

    @Override
    public boolean existsById(Long id) {
        return restaurantRepository.existsById(id);
    }

    @Override
    public Optional<Restaurant> findById(Long restaurantId) {
        return restaurantRepository
                .findById(restaurantId)
                .map(restaurantEntityMapper::toModel);
    }

    @Override
    public boolean existsByIdAndOwnerId(Long restaurantId, Long ownerId) {
        return restaurantRepository.existsByIdAndOwnerId(restaurantId, ownerId);
    }

    @Override
    public List<Restaurant> findAllOrderByNameAsc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findAllByOrderByNameAsc(pageable)
                .stream()
                .map(restaurantEntityMapper::toRestaurant)
                .toList();
    }




}