package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.model.PageModel;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Override
    public boolean existsByIdAndRestaurantIdAndActiveTrue(Long dishId, Long restaurantId) {
        return dishRepository.existsByIdAndRestaurantIdAndActiveTrue(dishId, restaurantId);
    }

    public PageModel<Dish> findMenuByRestaurant(Long restaurantId, int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size);

        Page<DishEntity> result = (category == null || category.isBlank())
                ? dishRepository.findByRestaurantIdAndActiveTrueOrderByNameAsc(restaurantId, pageable)
                : dishRepository.findByRestaurantIdAndCategoryAndActiveTrueOrderByNameAsc(restaurantId, category, pageable);

        List<Dish> content = result.getContent().stream()
                .map(dishEntityMapper::toDish)
                .toList();

        return new PageModel<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }
}

