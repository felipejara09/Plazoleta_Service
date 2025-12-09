package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IDishService;
import com.pragma.powerup.domain.api.IRestaurantService;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import com.pragma.powerup.domain.usecase.DishUseCase;
import com.pragma.powerup.domain.usecase.RestaurantUseCase;
import com.pragma.powerup.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UserExternalServiceAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort(
            IRestaurantRepository restaurantRepository,
            IRestaurantEntityMapper restaurantEntityMapper
    ) {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IUserExternalServicePort userExternalServicePort(UserExternalServiceAdapter adapter) {
        return adapter;
    }

    @Bean
    public IRestaurantService restaurantService(
            IRestaurantPersistencePort restaurantPersistencePort,
            IUserExternalServicePort userExternalServicePort
    ) {
        return new RestaurantUseCase(restaurantPersistencePort, userExternalServicePort);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort(IDishRepository dishRepository,
                                                    IDishEntityMapper dishEntityMapper) {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IDishService dishService(IDishPersistencePort dishPersistencePort,
                                    IRestaurantPersistencePort restaurantPersistencePort) {
        return new DishUseCase(dishPersistencePort, restaurantPersistencePort);
    }
}