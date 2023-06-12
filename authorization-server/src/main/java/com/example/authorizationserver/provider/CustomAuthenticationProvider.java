package com.example.authorizationserver.provider;

import com.example.authorizationserver.service.CustomUserDetailService;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        // OAuth2 의존성을 사용하는 경우 CustomWebAuthenticationDetails 를 만들지 않아도  authentication.getDetails() 에서 추가 파라미터를 추출할 수 있다
        LinkedHashMap<String, String> details = (LinkedHashMap<String, String>) authentication.getDetails();
        if (isSocialLogin(details)) {
            return getSocialLoginToken(authentication, details);
        }
        return getDefaultLoginToken(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // Authentication과 해당 토큰이 같을 때 구동
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean isSocialLogin(LinkedHashMap<String, String> details) {
        return details.containsKey("snsSync");
    }

    private UsernamePasswordAuthenticationToken getSocialLoginToken(Authentication authentication,
        LinkedHashMap<String, String> details) {
        String username = (String) authentication.getPrincipal();
        String snsSync = details.get("snsSync");
        String secretKey = details.get("snsSecretKey");
        UserDetails userDetails = userDetailsService.loadUserByUsernameAndSnsSync(username,
            snsSync);

        if (ObjectUtils.isEmpty(userDetails)) {
            throw new BadCredentialsException("Invalid UserInfo");
        }
        if (!passwordEncoder.matches(secretKey, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid secretKey");
        }
        return new UsernamePasswordAuthenticationToken(username, secretKey,
            userDetails.getAuthorities());
    }

    private UsernamePasswordAuthenticationToken getDefaultLoginToken(
        Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (ObjectUtils.isEmpty(userDetails)) {
            throw new BadCredentialsException("Invalid UserInfo");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(username, password,
            userDetails.getAuthorities());
    }

}
