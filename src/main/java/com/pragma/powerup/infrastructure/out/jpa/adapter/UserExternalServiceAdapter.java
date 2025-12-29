package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserExternalServiceAdapter implements IUserExternalServicePort {

    private final RestTemplate restTemplate;

    @Value("${services.user.base-url}")
    private String usersBaseUrl;

    @Override
    public boolean isOwnerUser(Long userId) {
       return true;
    }

    @Override
    public String getClientPhoneNumber(String token, Long clientId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = usersBaseUrl + "/api/v1/users/" + clientId + "/phone";

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
