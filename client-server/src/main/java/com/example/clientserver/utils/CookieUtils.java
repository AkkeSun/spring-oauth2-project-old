package com.example.clientserver.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtils {

    private final HttpServletResponse response;

    public void setCookie(String key, String value){
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*24);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        response.addCookie(cookie);
    }

}
