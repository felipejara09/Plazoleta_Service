package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
    boolean existsByNitId(String nitId);
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
    Page<RestaurantEntity> findAllByOrderByNameAsc(Pageable pageable);
}