package com.pragma.powerup.infrastructure.out.user.adapter;

import com.pragma.powerup.domain.spi.IEmployeeRestaurantPort;
import com.pragma.powerup.infrastructure.out.user.dto.EmployeeRestaurantResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EmployeeRestaurantHttpAdapter implements IEmployeeRestaurantPort {

    private final RestTemplate restTemplate;

    @Value("${services.user.base-url}")
    private String userBaseUrl;


    @Override
    public Long getMyRestaurantId(String token) {
        String url = userBaseUrl + "/api/v1/internal/employees/me/restaurant";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<EmployeeRestaurantResponseDto> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, EmployeeRestaurantResponseDto.class);

        return response.getBody().getRestaurantId();
    }
}
