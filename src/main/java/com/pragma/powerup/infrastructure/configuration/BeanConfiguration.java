package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IDishService;
import com.pragma.powerup.domain.api.IRestaurantService;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import com.pragma.powerup.domain.usecase.DishUseCase;
import com.pragma.powerup.domain.usecase.RestaurantUseCase;
import com.pragma.powerup.domain.validation.DishBusinessValidator;
import com.pragma.powerup.domain.validation.DishDataValidator;
import com.pragma.powerup.domain.validation.RestaurantBusinessValidator;
import com.pragma.powerup.domain.validation.RestaurantDataValidator;
import com.pragma.powerup.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UserExternalServiceAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {


    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort(
            IRestaurantRepository repository,
            IRestaurantEntityMapper mapper
    ) {
        return new RestaurantJpaAdapter(repository, mapper);
    }

    @Bean
    public RestaurantDataValidator restaurantDataValidator() {
        return new RestaurantDataValidator();
    }

    @Bean
    public RestaurantBusinessValidator restaurantBusinessValidator(
            IRestaurantPersistencePort restaurantPersistencePort,
            IUserExternalServicePort userExternalServicePort
    ) {
        return new RestaurantBusinessValidator(
                restaurantPersistencePort,
                userExternalServicePort
        );
    }

    @Bean
    public IRestaurantService restaurantService(
            IRestaurantPersistencePort restaurantPersistencePort,
            RestaurantDataValidator dataValidator,
            RestaurantBusinessValidator businessValidator
    ) {
        return new RestaurantUseCase(
                restaurantPersistencePort,
                dataValidator,
                businessValidator
        );
    }

    @Bean
    public IDishPersistencePort dishPersistencePort(
            IDishRepository repository,
            IDishEntityMapper mapper
    ) {
        return new DishJpaAdapter(repository, mapper);
    }

    @Bean
    public DishDataValidator dishDataValidator() {
        return new DishDataValidator();
    }

    @Bean
    public DishBusinessValidator dishBusinessValidator(
            IRestaurantPersistencePort restaurantPersistencePort
    ) {
        return new DishBusinessValidator(restaurantPersistencePort);
    }

    @Bean
    public IDishService dishService(
            IDishPersistencePort dishPersistencePort,
            DishDataValidator dataValidator,
            DishBusinessValidator businessValidator
    ) {
        return new DishUseCase(
                dishPersistencePort,
                dataValidator,
                businessValidator
        );
    }


    @Bean
    public IUserExternalServicePort userExternalServicePort(
            UserExternalServiceAdapter adapter
    ) {
        return adapter;
    }
}
