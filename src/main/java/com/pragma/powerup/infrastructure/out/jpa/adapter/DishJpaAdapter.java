package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Override
    public Dish save(Dish dish) {
        DishEntity entity = dishEntityMapper.toEntity(dish);
        DishEntity saved = dishRepository.save(entity);
        return dishEntityMapper.toDish(saved);
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id)
                .map(dishEntityMapper::toDish);
    }
}
