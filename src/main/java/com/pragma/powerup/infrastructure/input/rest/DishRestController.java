package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.dto.response.DishMenuResponseDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PageResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DishRestController {

    private final IDishHandler dishHandler;

    @Operation(
            summary = "Create dish",
            description = "Allows a restaurant owner to create a new dish associated with their restaurant."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Dish created successfully",
                    content = @Content(schema = @Schema(implementation = DishResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or business rules violated",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only restaurant owners can create dishes",
                    content = @Content
            )
    })

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/owner/dishes")
    public ResponseEntity<Void> createDish(@Validated @RequestBody DishRequestDto dishRequestDto) {
        dishHandler.createDish(dishRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(
            summary = "Update dish",
            description = "Allows a restaurant owner to update the price and description of an existing dish."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Dish updated successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or business rules violated",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only restaurant owners can update dishes",
                    content = @Content
            )
    })

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/owner/dishes/update/{id}")
    public ResponseEntity<Void> updateDish(@PathVariable Long id,
                                           @Validated @RequestBody DishUpdateRequestDto dishUpdateRequestDto) {
        dishHandler.updateDish(id, dishUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Change dish availability status",
            description = "Allows a restaurant owner to activate or deactivate a dish."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Dish status updated successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dish not found or invalid status value",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only restaurant owners can change dish status",
                    content = @Content
            )
    })


    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("owner/dishes/{dishId}/status")
    public ResponseEntity<Void> changeDishStatus(@PathVariable Long dishId,
                                                 @RequestParam Boolean active) {
        dishHandler.changeDishStatus(dishId, active);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "List restaurant menu",
            description = "Allows a client to view the active dishes of a restaurant with pagination and optional category filtering."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurant menu retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurant not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Only clients can access restaurant menus",
                    content = @Content
            )
    })


    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("client/restaurants/{restaurantId}/dishes")
    public ResponseEntity<PageResponseDto<DishMenuResponseDto>> listDishesByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(dishHandler.listMenu(restaurantId, page, size, category));

    }



}
