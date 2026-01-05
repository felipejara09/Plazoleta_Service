package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.spi.ITraceabilityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TraceabilityHttpAdapter implements ITraceabilityPort {

    private final RestTemplate restTemplate;

    @Value("${services.traceability.base-url}")
    private String traceabilityBaseUrl;

    @Override
    public void registerStatusChange(
            String token,
            Long orderId,
            Long clientId,
            Long restaurantId,
            String previousStatus,
            String newStatus,
            Long changedByUserId,
            String changedByRole
    ) {
        String url = traceabilityBaseUrl + "/api/v1/internal/trace-logs";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null && !token.isBlank()) {
            if (token.startsWith("Bearer ")) {
                headers.setBearerAuth(token.substring(7));
            } else {
                headers.setBearerAuth(token);
            }
        }

                String body = """
                {
                  "orderId": %d,
                  "clientId": %d,
                  "restaurantId": %d,
                  "previousStatus": "%s",
                  "newStatus": "%s",
                  "changedByUserId": %d,
                  "changedByRole": "%s"
                }
                """.formatted(orderId, clientId, restaurantId, previousStatus, newStatus, changedByUserId, changedByRole);

        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), Void.class);
    }
}
