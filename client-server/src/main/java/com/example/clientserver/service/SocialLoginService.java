package com.example.clientserver.service;

import com.example.clientserver.domain.SocialLoginUser;
import com.example.clientserver.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final JsonUtils jsonUtils;
    private final TokenService tokenService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        // 소셜 로그인 인가서버에서 가져온 사용자 정보로 Oauth2User 생성
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        // 회원가입
        register(new SocialLoginUser(oAuth2User, clientRegistration.getRegistrationId()));

        // 인증을 위한 OAuth2User 리턴
        return oAuth2User;
    }

    public void setDefaultToken(@AuthenticationPrincipal OAuth2User oAuth2User,
        Authentication authentication) {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = authenticationToken.getAuthorizedClientRegistrationId();
        SocialLoginUser loginUser = new SocialLoginUser(oAuth2User, registrationId);
        JSONObject token = tokenService.getAccessTokenFromSocialLogin(loginUser);
    }

    private void register(SocialLoginUser socialLoginUser) {
        Mono<String> registerCheckMono = WebClient.create().post()
            .uri("http://localhost:9000/registerCheck")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(socialLoginUser)
            .retrieve()
            .bodyToMono(String.class);
        JSONObject checkResult = jsonUtils.getJsonResponse(registerCheckMono.block());
        if ("N".equals(checkResult.getAsString("result"))) {
            Mono<String> registerMono = WebClient.create().post()
                .uri("http://localhost:9000/user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(socialLoginUser)
                .retrieve()
                .bodyToMono(String.class);
            JSONObject registerResult = jsonUtils.getJsonResponse(registerMono.block());
            if ("Y".equals(registerResult.getAsString("result"))) {
                log.info("[register] : type: {}, userId: {} ",
                    socialLoginUser.getSnsSync(), registerResult.getAsString("message"));
            }
        }
    }
}
