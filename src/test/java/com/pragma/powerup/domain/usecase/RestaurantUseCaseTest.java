package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.PageModel;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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


    @Test
    void listRestaurants_success_shouldCallPortWithPagination() {
        Restaurant r1 = new Restaurant(null, "Arepas", "1", "x", "+57", "logo1", 1L);
        Restaurant r2 = new Restaurant(null, "Burger", "2", "y", "+57", "logo2", 2L);

        PageModel<Restaurant> page = new PageModel<>(
                List.of(r1, r2),
                0, 2,
                20L, 10,
                true, false
        );

        when(restaurantPersistencePort.findAllOrderByNameAsc(0, 2))
                .thenReturn(page);

        PageModel<Restaurant> result = restaurantUseCase.listRestaurants(0, 2);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Arepas", result.getContent().get(0).getName());
        assertEquals("Burger", result.getContent().get(1).getName());

        // metadata
        assertEquals(20L, result.getTotalElements());
        assertEquals(10, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.hasNext());

        verify(restaurantPersistencePort).findAllOrderByNameAsc(0, 2);
        verifyNoInteractions(userExternalServicePort);
    }

    @Test
    void listRestaurants_whenEmpty_shouldReturnEmptyPage() {
        PageModel<Restaurant> empty = new PageModel<>(
                List.of(),
                0, 10,
                0L, 0,
                true, true
        );

        when(restaurantPersistencePort.findAllOrderByNameAsc(0, 10))
                .thenReturn(empty);

        PageModel<Restaurant> result = restaurantUseCase.listRestaurants(0, 10);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.isLast());

        verify(restaurantPersistencePort).findAllOrderByNameAsc(0, 10);
        verifyNoInteractions(userExternalServicePort);
    }

    @Test
    void listRestaurants_invalidPage_shouldThrowInvalidPaginationException() {
        assertThrows(InvalidPaginationException.class,
                () -> restaurantUseCase.listRestaurants(-1, 10));

        verifyNoInteractions(restaurantPersistencePort);
    }

    @Test
    void listRestaurants_invalidSize_shouldThrowInvalidPaginationException() {
        assertThrows(InvalidPaginationException.class,
                () -> restaurantUseCase.listRestaurants(0, 0));

        verifyNoInteractions(restaurantPersistencePort);
    }
}
