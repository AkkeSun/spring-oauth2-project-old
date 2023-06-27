# [OAuth2 Resource Server]

수정일 : 2023.06.27

* Spring Security 5 이전 버전으로 개발한 OAuth2 Resource Server 입니다
* org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.6.8
* 커스텀 인증 서버의 토큰값으로 사용자를 검증합니다.
* 권한이 필요한 리소스에 접근시 해더값을 전송해야 합니다

  | Header Key     | Header Value                 |
      |----------------|------------------------------|
  | Authorization  | Bearer {Access token String} |

<br>