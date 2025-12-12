package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.validation.DishBusinessValidator;
import com.pragma.powerup.domain.validation.DishDataValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishUseCaseTest {

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    private DishUseCase dishUseCase;

    private Dish validDish;

    @BeforeEach
    void setUp() {
        DishDataValidator dataValidator = new DishDataValidator();
        DishBusinessValidator businessValidator = new DishBusinessValidator(restaurantPersistencePort);

        dishUseCase = new DishUseCase(dishPersistencePort, dataValidator, businessValidator);
        validDish = createValidDish();
    }

    private Dish createValidDish() {
        Dish dish = new Dish();
        dish.setId(null);
        dish.setName("Hamburguesa Clasica");
        dish.setPrice(20000);
        dish.setDescription("Hamburguesa con queso y tocineta");
        dish.setImageUrl("https://example.com/hamburguesa.png");
        dish.setActive(true);
        dish.setRestaurantId(1L);
        return dish;
    }

    @Test
    void createDish_success() {
        when(restaurantPersistencePort.existsById(validDish.getRestaurantId())).thenReturn(true);

        dishUseCase.createDish(validDish);

        verify(restaurantPersistencePort).existsById(validDish.getRestaurantId());
        verify(dishPersistencePort).save(validDish);
    }

    @Test
    void createDish_nullActive_setsActiveTrueByDefault() {
        validDish.setActive(null);
        when(restaurantPersistencePort.existsById(validDish.getRestaurantId())).thenReturn(true);

        dishUseCase.createDish(validDish);

        assertTrue(validDish.getActive());
        verify(dishPersistencePort).save(validDish);
    }

    @Test
    void createDish_blankName_throwsInvalidDishNameException() {
        validDish.setName("   ");

        assertThrows(InvalidDishNameException.class,
                () -> dishUseCase.createDish(validDish));

        verifyNoInteractions(restaurantPersistencePort);
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void createDish_nameOnlyNumbers_throwsInvalidDishNameException() {
        validDish.setName("123456");

        assertThrows(InvalidDishNameException.class,
                () -> dishUseCase.createDish(validDish));

        verifyNoInteractions(restaurantPersistencePort);
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void createDish_nullPrice_throwsInvalidDishPriceException() {
        validDish.setPrice(null);

        assertThrows(InvalidDishPriceException.class,
                () -> dishUseCase.createDish(validDish));

        verifyNoInteractions(restaurantPersistencePort);
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void createDish_invalidPrice_throwsInvalidDishPriceException() {
        validDish.setPrice(0);

        assertThrows(InvalidDishPriceException.class,
                () -> dishUseCase.createDish(validDish));

        verifyNoInteractions(restaurantPersistencePort);
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void createDish_blankDescription_throwsInvalidDishDescriptionException() {
        validDish.setDescription("   ");

        assertThrows(InvalidDishDescriptionException.class,
                () -> dishUseCase.createDish(validDish));

        verifyNoInteractions(restaurantPersistencePort);
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void createDish_invalidImageUrl_throwsInvalidDishImageUrlException() {
        validDish.setImageUrl("htp:/mala-url");

        assertThrows(InvalidDishImageUrlException.class,
                () -> dishUseCase.createDish(validDish));

        verifyNoInteractions(restaurantPersistencePort);
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void createDish_nullRestaurantId_throwsRestaurantNotFoundException() {
        validDish.setRestaurantId(null);

        assertThrows(RestaurantNotFoundException.class,
                () -> dishUseCase.createDish(validDish));

        verify(restaurantPersistencePort, never()).existsById(any());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void createDish_restaurantDoesNotExist_throwsRestaurantNotFoundException() {
        when(restaurantPersistencePort.existsById(validDish.getRestaurantId())).thenReturn(false);

        assertThrows(RestaurantNotFoundException.class,
                () -> dishUseCase.createDish(validDish));

        verify(restaurantPersistencePort).existsById(validDish.getRestaurantId());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void updateDish_success() {
        Dish existingDish = createValidDish();
        existingDish.setId(1L);

        when(dishPersistencePort.findById(1L)).thenReturn(Optional.of(existingDish));

        dishUseCase.updateDish(1L, 30000, "Descripcion actualizada");

        assertEquals(30000, existingDish.getPrice());
        assertEquals("Descripcion actualizada", existingDish.getDescription());
        verify(dishPersistencePort).save(existingDish);
    }

    @Test
    void updateDish_dishNotFound_shouldThrowDishNotFoundException() {
        when(dishPersistencePort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class,
                () -> dishUseCase.updateDish(1L, 20000, "Nueva desc"));

        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void updateDish_nullPrice_shouldThrowInvalidDishPriceException() {
        Dish existingDish = createValidDish();
        existingDish.setId(1L);

        when(dishPersistencePort.findById(1L)).thenReturn(Optional.of(existingDish));

        assertThrows(InvalidDishPriceException.class,
                () -> dishUseCase.updateDish(1L, null, "Nueva descripcion"));

        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void updateDish_invalidPrice_shouldThrowInvalidDishPriceException() {
        Dish existingDish = createValidDish();
        existingDish.setId(1L);

        when(dishPersistencePort.findById(1L)).thenReturn(Optional.of(existingDish));

        assertThrows(InvalidDishPriceException.class,
                () -> dishUseCase.updateDish(1L, 0, "Nueva desc"));

        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void updateDish_nullDescription_shouldThrowInvalidDishDescriptionException() {
        Dish existingDish = createValidDish();
        existingDish.setId(1L);

        when(dishPersistencePort.findById(1L)).thenReturn(Optional.of(existingDish));

        assertThrows(InvalidDishDescriptionException.class,
                () -> dishUseCase.updateDish(1L, 20000, null));

        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void updateDish_blankDescription_shouldThrowInvalidDishDescriptionException() {
        Dish existingDish = createValidDish();
        existingDish.setId(1L);

        when(dishPersistencePort.findById(1L)).thenReturn(Optional.of(existingDish));

        assertThrows(InvalidDishDescriptionException.class,
                () -> dishUseCase.updateDish(1L, 20000, "   "));

        verify(dishPersistencePort, never()).save(any());
    }
}
