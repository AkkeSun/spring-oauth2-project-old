package com.example.clientserver.config;

import com.example.clientserver.service.SocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig {

    private final SocialLoginService customOAuth2UserService;

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/", "/logout", "/socialLogin", "/userLogin").permitAll()
            .anyRequest().authenticated();

        http.logout(logout -> {
            logout.deleteCookies("accessToken", "refreshToken");
            logout.logoutSuccessUrl("/");
        });

        // ------- 디폴트 로그인 세팅 (Spring OAauth2 Login)------
        http.oauth2Client(Customizer.withDefaults());

        // ------- 소셜 로그인 세팅 -------
        http.oauth2Login(oauth2 ->
            oauth2.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(
                customOAuth2UserService)));
        http.oauth2Login().defaultSuccessUrl("/socialLogin");
        return http.build();
    }
}

