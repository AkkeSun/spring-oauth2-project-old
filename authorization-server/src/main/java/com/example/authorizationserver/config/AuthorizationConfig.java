package com.example.authorizationserver.config;

import com.example.authorizationserver.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@RequiredArgsConstructor
@EnableAuthorizationServer
@Configuration
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    private final TokenStore tokenStore;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final CustomUserDetailService userDetailsService;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
            .withClient("oauth2-test")
            .secret(passwordEncoder.encode("pass"))
            .authorizedGrantTypes("authorization_code", "password", "refresh_token", "client_credentials")
            .scopes("read", "write")
            .redirectUris("http://127.0.0.1:8081")
            .accessTokenValiditySeconds(60*60)
            .refreshTokenValiditySeconds(6*60*60)
            .autoApprove(true); // scope 확인 요청 안함
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore)
            .authenticationManager(authenticationManager)
            .userDetailsService(userDetailsService);
    }
}
