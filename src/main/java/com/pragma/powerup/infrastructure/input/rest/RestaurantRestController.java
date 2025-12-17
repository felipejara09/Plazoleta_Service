package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantListResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.handler.IRestaurantOwnershipHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RestaurantRestController {

    private final IRestaurantHandler restaurantHandler;
    private final IRestaurantOwnershipHandler ownershipHandler;

    @Operation(
            summary = "Crear restaurante",
            description = "Permite al administrador registrar un restaurante con su propietario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante creado correctamente",
                    content = @Content(schema = @Schema(implementation = RestaurantResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o reglas de negocio incumplidas",
                    content = @Content)
    })

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponseDto createRestaurant(@Valid @RequestBody RestaurantRequestDto dto) {
        return restaurantHandler.createRestaurant(dto);
    }

    // no publica para el
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/restaurants/{restaurantId}/ownership")
    public ResponseEntity<Boolean> ownership(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(ownershipHandler.checkOwnership(restaurantId));
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("client/restaurants")
    public ResponseEntity<List<RestaurantListResponseDto>> listRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(restaurantHandler.listRestaurants(page, size));
    }

}