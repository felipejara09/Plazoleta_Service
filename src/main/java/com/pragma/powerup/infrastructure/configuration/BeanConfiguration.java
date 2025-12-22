package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IDishService;
import com.pragma.powerup.domain.api.IOrderService;
import com.pragma.powerup.domain.api.IRestaurantService;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import com.pragma.powerup.domain.usecase.DishUseCase;
import com.pragma.powerup.domain.usecase.OrderUseCase;
import com.pragma.powerup.domain.usecase.RestaurantUseCase;
import com.pragma.powerup.domain.validation.*;
import com.pragma.powerup.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UserExternalServiceAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

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

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort(OrderJpaAdapter adapter) {
        return adapter;
    }

    @Bean
    public OrderDataValidator orderDataValidator() {
        return new OrderDataValidator();
    }

    @Bean
    public OrderBusinessValidator orderBusinessValidator(
            IOrderPersistencePort orderPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IDishPersistencePort dishPersistencePort
    ) {
        return new OrderBusinessValidator(orderPersistencePort, restaurantPersistencePort, dishPersistencePort);
    }

    @Bean
    public IOrderService orderService(
            IOrderPersistencePort orderPersistencePort,
            OrderDataValidator dataValidator,
            OrderBusinessValidator businessValidator
    ) {
        return new OrderUseCase(orderPersistencePort, dataValidator, businessValidator);
    }

}
