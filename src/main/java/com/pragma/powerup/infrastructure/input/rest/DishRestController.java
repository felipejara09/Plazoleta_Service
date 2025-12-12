package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
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
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
public class DishRestController {

    private final IDishHandler dishHandler;

    @Operation(
            summary = "Crear plato",
            description = "Permite al propietario de un restaurante crear platos asociados a su restaurante."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plato creado correctamente",
                    content = @Content(schema = @Schema(implementation = DishResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o reglas de negocio incumplidas",
                    content = @Content)
    })
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/dishes")
    public ResponseEntity<Void> createDish(@Validated @RequestBody DishRequestDto dishRequestDto) {
        dishHandler.createDish(dishRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(
            summary = "ACtualizar plato",
            description = "Permite al propietario de un restaurante Actualizar precio y descripcion del plato."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Plato actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = DishResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o reglas de negocio incumplidas",
                    content = @Content)
    })
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/dishes/update/{id}")
    public ResponseEntity<Void> updateDish(@PathVariable Long id,
                                           @Validated @RequestBody DishUpdateRequestDto dishUpdateRequestDto) {
        dishHandler.updateDish(id, dishUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
