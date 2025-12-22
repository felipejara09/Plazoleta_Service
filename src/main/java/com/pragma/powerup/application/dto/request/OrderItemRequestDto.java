package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class OrderItemRequestDto {
    @NotNull
    private Long dishId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
