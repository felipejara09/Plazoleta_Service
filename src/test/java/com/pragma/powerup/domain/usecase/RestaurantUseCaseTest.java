package com.pragma.powerup.domain.usecase;


import com.pragma.powerup.domain.exception.InvalidLogoUrlException;
import com.pragma.powerup.domain.exception.InvalidRestaurantNameException;
import com.pragma.powerup.domain.exception.InvalidRestaurantNitException;
import com.pragma.powerup.domain.exception.InvalidRestaurantPhoneException;
import com.pragma.powerup.domain.exception.OwnerIsNotOwnerRoleException;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserExternalServicePort userExternalServicePort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        validRestaurant = createValidRestaurant();
    }

    private Restaurant createValidRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(null);
        restaurant.setName("ElectroBurger 2");
        restaurant.setNitId("123456789");
        restaurant.setAddress("Calle 123 #45-67");
        restaurant.setPhoneNumber("+573001112233");
        restaurant.setLogoUrl("https://example.com/logo.png");
        restaurant.setOwnerId(1L);
        return restaurant;
    }

    @Test
    void createRestaurant_success() {

        when(restaurantPersistencePort.existsByNitId(validRestaurant.getNitId())).thenReturn(false);
        when(userExternalServicePort.isOwnerUser(validRestaurant.getOwnerId())).thenReturn(true);


        restaurantUseCase.createRestaurant(validRestaurant);


        verify(restaurantPersistencePort, times(1)).save(validRestaurant);
    }

    @Test
    void createRestaurant_nameOnlyNumbers_shouldThrowInvalidRestaurantNameException() {

        validRestaurant.setName("123456");


        assertThrows(InvalidRestaurantNameException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_emptyName_shouldThrowInvalidRestaurantNameException() {
        validRestaurant.setName("   ");

        assertThrows(InvalidRestaurantNameException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_invalidNit_shouldThrowInvalidRestaurantNitException() {

        validRestaurant.setNitId("12A345");


        assertThrows(InvalidRestaurantNitException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_nitAlreadyExists_shouldThrowInvalidRestaurantNitException() {

        when(restaurantPersistencePort.existsByNitId(validRestaurant.getNitId())).thenReturn(true);
        when(userExternalServicePort.isOwnerUser(validRestaurant.getOwnerId())).thenReturn(true);

        assertThrows(InvalidRestaurantNitException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_invalidPhone_shouldThrowInvalidRestaurantPhoneException() {

        validRestaurant.setPhoneNumber("12345");

        assertThrows(InvalidRestaurantPhoneException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_invalidLogoUrl_shouldThrowInvalidLogoUrlException() {

        when(userExternalServicePort.isOwnerUser(validRestaurant.getOwnerId())).thenReturn(true);
        when(restaurantPersistencePort.existsByNitId(validRestaurant.getNitId())).thenReturn(false);

        validRestaurant.setLogoUrl("htp:/mala-url");

        assertThrows(InvalidLogoUrlException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_ownerIsNotOwnerRole_shouldThrowOwnerIsNotOwnerRoleException() {
        when(restaurantPersistencePort.existsByNitId(validRestaurant.getNitId())).thenReturn(false);
        when(userExternalServicePort.isOwnerUser(validRestaurant.getOwnerId())).thenReturn(false);

        assertThrows(OwnerIsNotOwnerRoleException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort, never()).save(any());
    }
}
