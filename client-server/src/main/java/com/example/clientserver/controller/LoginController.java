package com.example.clientserver.controller;

import com.example.clientserver.service.DefaultLoginService;
import com.example.clientserver.service.SocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final DefaultLoginService defaultLoginService;
    private final SocialLoginService socialLoginService;


    /*
        username, password 로 로그인
        @RegisteredOAuth2AuthorizedClient 설정시 인가서버에서 인가 처리작업을 자동으로 해준다
     */
    @PostMapping("/userLogin")
    public String userLogin(
        @RegisteredOAuth2AuthorizedClient("spring") OAuth2AuthorizedClient authorize) {
        defaultLoginService.login(authorize);
        return "index";
    }

    /*
        소셜 로그인 후 리다이렉트 되는 엔드포인트
        리소스 서버를 접근하는 인가서버에서 token 을 발급받는다
     */
    @GetMapping("/socialLogin")
    public String socialLogin(@AuthenticationPrincipal OAuth2User oAuth2User,
        Authentication authentication) {
        if (authentication != null) {
            socialLoginService.setDefaultToken(oAuth2User, authentication);
        }
        return "index";
    }
}
