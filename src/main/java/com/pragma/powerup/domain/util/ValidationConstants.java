package com.pragma.powerup.domain.util;

public final class ValidationConstants {

    private ValidationConstants() {}

    public static final String STATUS_INIT = "CREATED";
    public static final String PHONE_REGEX = "^\\+?\\d{10,13}$";
    public static final String ONLY_NUMBER_REGEX   = "^\\d+$";
    public static final String URL_REGEX = "^(https?://)([\\w.-]+)(:[0-9]{1,5})?(/.*)?$";
    public static final int MIN_PRICE = 1;
    public static final String NAME_REGEX = "^(?=.*[A-Za-zÁÉÍÓÚáéíóúñÑ])[\\p{L}\\p{N} ]{1,50}$";


}
