package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {
    boolean existsByClientIdAndStatusIn(Long clientId, Collection<String> statuses);
    Page<OrderEntity> findByRestaurantIdAndStatus(Long restaurantId, String status, Pageable pageable);
    Optional<OrderEntity> findById(Long id);
}
