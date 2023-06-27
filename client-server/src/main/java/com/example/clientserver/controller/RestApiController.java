package com.example.clientserver.controller;

import com.example.clientserver.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final ApiService restService;

    @GetMapping("/token")
    public Authentication token(@AuthenticationPrincipal OAuth2User oAuth2User,
        Authentication authentication) {

        log.info("============ 인가서버에서 가져온 정보 ============");
        log.info(oAuth2User.getName());
        log.info(oAuth2User.getAuthorities().toString());

        log.info("============ 스프링 시큐리티에 저장된 정보 ============");
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        log.info(token.getAuthorizedClientRegistrationId());
        log.info(token.getPrincipal().toString());
        log.info(token.getAuthorities().toString());

        return authentication;
    }

    @GetMapping("/getData")
    public ResponseEntity<String> getAuthInfo(
        @CookieValue(value = "accessToken", required = false) String accessToken,
        @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        return ResponseEntity.ok(restService.getApiData(accessToken, refreshToken, true));
    }
}
