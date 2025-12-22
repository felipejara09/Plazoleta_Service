package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter @Setter
public class OrderRequestDto {
    @NotNull
    private Long restaurantId;

    @Valid
    @NotNull
    private List<OrderItemRequestDto> items;
}
