package com.example.clientserver.service;

import com.example.clientserver.utils.CookieUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultLoginService {

    private final CookieUtils cookieUtils;

    public void login(OAuth2AuthorizedClient authClient) {
        if (authClient != null) {
            setAuthentication(authClient);
            cookieUtils.setCookie("accessToken", authClient.getAccessToken().getTokenValue());
            cookieUtils.setCookie("refreshToken", authClient.getRefreshToken().getTokenValue());
        }
    }

    // 시큐리티 인증 처리
    private void setAuthentication(OAuth2AuthorizedClient authorize) {

        ClientRegistration clientRegistration = authorize.getClientRegistration();
        OAuth2AccessToken accessToken = authorize.getAccessToken();
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessToken);

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("access_token", accessToken.getTokenValue());

        // 시큐리티에 저장할 인증 객채 생성
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        OAuth2AuthenticationToken oAuth2AuthenticationToken = new OAuth2AuthenticationToken(
            oAuth2User, oAuth2User.getAuthorities(),
            clientRegistration.getRegistrationId());

        // 인증
        SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);
    }
}
