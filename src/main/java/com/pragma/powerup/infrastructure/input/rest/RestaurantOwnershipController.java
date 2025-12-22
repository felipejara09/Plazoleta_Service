package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.handler.IRestaurantOwnershipHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor*/
public class RestaurantOwnershipController {

    /* private final IRestaurantOwnershipHandler ownershipHandler;

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/{restaurantId}/ownership")
    public ResponseEntity<Boolean> ownership(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(ownershipHandler.checkOwnership(restaurantId));
    }*/
}
