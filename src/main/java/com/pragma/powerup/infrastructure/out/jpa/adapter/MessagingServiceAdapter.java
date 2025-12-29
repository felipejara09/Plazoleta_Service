package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.spi.IMessagingPort;
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
public class MessagingServiceAdapter implements IMessagingPort {

    private final RestTemplate restTemplate;

    @Value("${services.messaging.base-url}")
    private String messagingBaseUrl;

    @Override
    public void sendOrderReadySms(String token, String phoneNumber, String message) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        String url = messagingBaseUrl + "/api/v1/sms/send";

        String body = """
                {
                  "phoneNumber": "%s",
                  "message": "%s"
                }
                """.formatted(phoneNumber, message.replace("\"","\\\""));

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }
}
