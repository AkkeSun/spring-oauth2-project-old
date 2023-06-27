package com.example.clientserver.domain;

import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@Setter
@NoArgsConstructor
public class SocialLoginUser {

    private String username;
    private String password;
    private String snsSync;
    private String snsSecret;
    private String role;
    private Map<String, Object> attributes;

    public SocialLoginUser(OAuth2User oAuth2User, String authServer) {
        this.password = UUID.randomUUID().toString();
        this.snsSync = authServer;
        this.role = "ROLE_USER";
        switch (this.snsSync) {
            case "kakao":
                this.attributes = getAttributes(oAuth2User, "kakao_account");
                this.username = (String) (attributes.get("email"));
                this.snsSecret = (String) (attributes.get("email"));
                break;
            case "naver":
                this.attributes = getAttributes(oAuth2User, "response");
                this.username = (String) attributes.get("email");
                this.snsSecret = (String) attributes.get("id");
                break;
            case "google":
                this.attributes = oAuth2User.getAttributes();
                this.username = (String) attributes.get("email");
                this.snsSecret = (String) attributes.get("sub");
        }
    }

    private Map<String, Object> getAttributes(OAuth2User oAuth2User, String key) {
        return (Map<String, Object>) oAuth2User.getAttributes().get(key);
    }
}
