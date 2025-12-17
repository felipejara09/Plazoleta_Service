package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {
    Page<DishEntity> findByRestaurantIdAndActiveTrueOrderByNameAsc(Long restaurantId, Pageable pageable);

    Page<DishEntity> findByRestaurantIdAndCategoryAndActiveTrueOrderByNameAsc(Long restaurantId, String category, Pageable pageable);

}
