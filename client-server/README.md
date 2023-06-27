# [OAuth2 Client Server]

수정일 : 2023.06.27

* Spring Security 5 버전으로 개발한 OAuth2 Client Server 입니다
* org.springframework.boot:spring-boot-starter-oauth2-client
* 인증 서버 : 커스텀 서버, naver, google, kakao
* 소셜 로그인을 하는 경우에도 자체 리소스 서버에 접근할 수 있도록 소셜 로그인 후 커스텀 서버 토큰을 받도록 개발하였습니다

<br/> 

--------------------------------

## 1. 커스텀 서버를 통한 인증 프로세스

- oauth2Client 를 사옹해 password 방식으로 커스텀서버에서 인가처리된 토큰을 발급 받습니다.
- 토큰 발급에 성공하면 커스텀 서버에서 사용자 정보 엔드포인트 (/userinfo)를 통해 디코딩된 토큰 정보를 추출합니다.
- 해당 정보를 기반으로 클라이언트 서버 인증처리를 합니다.
- 인증처리가 완료되면 AccessToken, RefreshToken 을 쿠키로 저장합니다.
- 리소스 서버와 통신할 때 Header 값으로 AccessToken 을 전송합니다.
- 토큰의 유효기간이 만료된 경우 커스텀 서버에 refreshToken 방식으로 AccessToken 을 재발급 받습니다.

<br>

## 2. 소셜 로그인 프로세스

- oauth2Login 을 를 사옹해 소셜 로그인 서버에서 인가처리된 정보를 받아옵니다.
- 인가처리에 성공한다면 받아온 사용자 정보를 표준화 한 후 커스텀 서버 가입을 진행합니다.
- 커스텀 서버에서 username, snsSync, snsSecret 값으로 인증 토큰을 발급받습니다.
- 인증처리가 완료되면 AccessToken, RefreshToken 을 쿠키로 저장합니다.
- 리소스 서버와 통신할 때 Header 값으로 AccessToken 을 전송합니다.
- 토큰의 유효기간이 만료된 경우 커스텀 서버에 refreshToken 방식으로 AccessToken 을 재발급 받습니다.

<br>