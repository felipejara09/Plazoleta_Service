package com.pragma.powerup.application.dto.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class RestaurantRequestDto {

    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "NIT is required")
    private String nitId;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "User is not an OWNER")
    private long ownerId;

    @NotBlank(message = "Logo URL is required")
    private String logoUrl;

}
