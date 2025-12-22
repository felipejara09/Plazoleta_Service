package com.pragma.powerup.application.dto.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class RestaurantRequestDto {

    @NotBlank
    private String name;
    @NotBlank
    private String nitId;
    @NotBlank
    private String address;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String logoUrl;
    @NotNull
    private Long ownerId;
}
