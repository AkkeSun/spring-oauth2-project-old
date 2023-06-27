package com.example.authorizationserver.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class MemberDTO {

    private String username;
    private String password;
    private String snsSync;
    private String snsSecret;
    private Role role;

    public void validate() {
        if (!StringUtils.hasText(this.username)) {
            throw new RuntimeException("username이 빈 값 입니다");
        }
        if (!StringUtils.hasText(this.password)) {
            throw new RuntimeException("password이 빈 값 입니다");
        }
        if (!StringUtils.hasText(this.role.toString())) {
            throw new RuntimeException("role이 빈 값 입니다");
        }
    }

    public void snsSyncValidate() {
        if (!StringUtils.hasText(this.username)) {
            throw new RuntimeException("username이 빈 값 입니다");
        }
        if (!StringUtils.hasText(this.password)) {
            throw new RuntimeException("password이 빈 값 입니다");
        }
        if (!StringUtils.hasText(this.role.toString())) {
            throw new RuntimeException("role이 빈 값 입니다");
        }
    }
}
