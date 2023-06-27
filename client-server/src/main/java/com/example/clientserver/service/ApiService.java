package com.example.clientserver.service;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final String RESOURCE_SERVER_HOST = "http://localhost:8082";
    private final TokenService tokenService;

    public String getApiData(String accessToken, String refreshToken, boolean isFirst) {
        try {
            return WebClient.create().get()
                .uri(RESOURCE_SERVER_HOST + "/test1")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                if ("Unauthorized".equals(e.getStatusText())) {
                    if (isFirst) {
                        JSONObject token = tokenService.getAccessTokenFromRefreshToken(
                            refreshToken);
                        return getApiData(token.getAsString("access_token"), refreshToken, false);
                    }
                    throw new RuntimeException("Unauthorized");
                }
                throw new RuntimeException("Access Denied");
            }
            throw new RuntimeException(e.getMessage());
        }
    }
}
