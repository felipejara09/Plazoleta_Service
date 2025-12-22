package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import com.pragma.powerup.domain.validation.RestaurantBusinessValidator;
import com.pragma.powerup.domain.validation.RestaurantDataValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserExternalServicePort userExternalServicePort;

    private RestaurantUseCase restaurantUseCase;

    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        RestaurantDataValidator dataValidator = new RestaurantDataValidator();
        RestaurantBusinessValidator businessValidator =
                new RestaurantBusinessValidator(restaurantPersistencePort, userExternalServicePort);

        restaurantUseCase = new RestaurantUseCase(restaurantPersistencePort, dataValidator, businessValidator);
        validRestaurant = createValidRestaurant();
    }

    private Restaurant createValidRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(null);
        restaurant.setName("ElectroBurger");
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

        verify(restaurantPersistencePort).save(validRestaurant);
    }


    @Test
    void createRestaurant_nameOnlyNumbers_shouldThrowInvalidRestaurantNameException() {
        validRestaurant.setName("123456");

        assertThrows(InvalidRestaurantNameException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verifyNoInteractions(restaurantPersistencePort);
        verifyNoInteractions(userExternalServicePort);
    }

    @Test
    void createRestaurant_emptyName_shouldThrowInvalidRestaurantNameException() {
        validRestaurant.setName("   ");

        assertThrows(InvalidRestaurantNameException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verifyNoInteractions(restaurantPersistencePort);
        verifyNoInteractions(userExternalServicePort);
    }

    @Test
    void createRestaurant_invalidNit_shouldThrowInvalidRestaurantNitException() {
        validRestaurant.setNitId("12A345");

        assertThrows(InvalidRestaurantNitException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verifyNoInteractions(restaurantPersistencePort);
        verifyNoInteractions(userExternalServicePort);
    }

    @Test
    void createRestaurant_invalidPhone_shouldThrowInvalidRestaurantPhoneException() {
        validRestaurant.setPhoneNumber("12345");

        assertThrows(InvalidRestaurantPhoneException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verifyNoInteractions(restaurantPersistencePort);
        verifyNoInteractions(userExternalServicePort);
    }

    @Test
    void createRestaurant_invalidLogoUrl_shouldThrowInvalidLogoUrlException() {
        validRestaurant.setLogoUrl("htp:/mala-url");

        assertThrows(InvalidLogoUrlException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verifyNoInteractions(restaurantPersistencePort);
        verifyNoInteractions(userExternalServicePort);
    }


    @Test
    void createRestaurant_nitAlreadyExists_shouldThrowInvalidRestaurantNitException() {
        when(restaurantPersistencePort.existsByNitId(validRestaurant.getNitId())).thenReturn(true);

        assertThrows(InvalidRestaurantNitException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort).existsByNitId(validRestaurant.getNitId());
        verify(userExternalServicePort, never()).isOwnerUser(any());
        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_ownerIsNotOwnerRole_shouldThrowOwnerIsNotOwnerRoleException() {
        when(restaurantPersistencePort.existsByNitId(validRestaurant.getNitId())).thenReturn(false);
        when(userExternalServicePort.isOwnerUser(validRestaurant.getOwnerId())).thenReturn(false);

        assertThrows(OwnerIsNotOwnerRoleException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant));

        verify(restaurantPersistencePort).existsByNitId(validRestaurant.getNitId());
        verify(userExternalServicePort).isOwnerUser(validRestaurant.getOwnerId());
        verify(restaurantPersistencePort, never()).save(any());
    }
}
