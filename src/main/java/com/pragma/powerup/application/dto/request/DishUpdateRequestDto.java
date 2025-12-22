package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DishUpdateRequestDto {


    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than zero")
    private Integer price;

    @NotBlank(message = "Description is required")
    private String description;

}

