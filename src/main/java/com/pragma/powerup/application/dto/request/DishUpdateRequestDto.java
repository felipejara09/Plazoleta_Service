package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DishUpdateRequestDto {


    @NotNull
    private Long ownerId;

    @Min(1)
    private Integer price;

    @NotBlank
    private String description;

}

