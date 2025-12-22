package com.pragma.powerup.domain.util;

public final class RestaurantValidationConstants {

    private RestaurantValidationConstants() {}

    public static final String PHONE_REGEX = "^\\+?\\d{10,13}$";
    public static final String NIT_REGEX   = "^\\d+$";
    public static final String LOGO_URL_REGEX = "^(https?://)([\\w.-]+)(:[0-9]{1,5})?(/.*)?$";
}
