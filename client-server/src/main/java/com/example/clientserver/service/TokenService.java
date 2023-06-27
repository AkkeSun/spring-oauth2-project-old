package com.example.clientserver.service;

import com.example.clientserver.domain.SocialLoginUser;
import com.example.clientserver.utils.CookieUtils;
import com.example.clientserver.utils.JsonUtils;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final String AUTH_SERVER_HOST = "http://localhost:9000/oauth/token";
    private final CookieUtils cookieUtils;
    private final JsonUtils jsonUtils;


    public JSONObject getAccessTokenFromRefreshToken(String refreshToken) {
        ClientRegistration spring = clientRegistrationRepository.findByRegistrationId("spring");
        JSONObject token = getAccessTokenFromRefreshToken(spring, refreshToken);
        setTokenCookies(token);
        return token;
    }

    public JSONObject getAccessTokenFromSocialLogin(SocialLoginUser loginUser) {
        ClientRegistration spring = clientRegistrationRepository.findByRegistrationId("spring");
        JSONObject token = getAccessTokenFromSocialLogin(spring, loginUser);
        setTokenCookies(token);
        return token;
    }

    private JSONObject getAccessTokenFromRefreshToken(ClientRegistration clientRegistration,
        String refreshToken) {
        System.out.println("reissue token from refresh token");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);

        String response = WebClient.create().post()
            .uri(AUTH_SERVER_HOST)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("Authorization", getBasicCredentials(clientRegistration))
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(String.class)
            .onErrorMap(throwable -> {
                throw new RuntimeException(throwable);
            })
            .block();
        return jsonUtils.getJsonResponse(response);
    }

    private JSONObject getAccessTokenFromSocialLogin(ClientRegistration clientRegistration,
        SocialLoginUser loginUser) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("username", loginUser.getUsername());
        formData.add("snsSync", loginUser.getSnsSync());
        formData.add("snsSecretKey", loginUser.getSnsSecret());

        String response = WebClient.create().post()
            .uri(AUTH_SERVER_HOST)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("Authorization", getBasicCredentials(clientRegistration))
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(String.class)
            .onErrorMap(throwable -> {
                System.out.println(throwable.getMessage());
                throw new RuntimeException(throwable);
            })
            .block();

        return jsonUtils.getJsonResponse(response);
    }


    private void setTokenCookies(JSONObject token) {
        cookieUtils.setCookie("accessToken", token.getAsString("access_token"));
        cookieUtils.setCookie("refreshToken", token.getAsString("refresh_token"));
    }

    private String getBasicCredentials(ClientRegistration clientRegistration) {
        return "Basic " + Base64.getEncoder().encodeToString((clientRegistration.getClientId() + ":"
            + clientRegistration.getClientSecret()).getBytes());
    }
}
