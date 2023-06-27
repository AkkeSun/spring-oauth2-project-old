# [OAuth2 Authorization Server]

수정일 : 2023.06.27

* Spring Security 5 이전 버전으로 개발한 OAuth2 Authorization Server 입니다
* org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.6.8
* JWT 토큰을 리턴합니다
* 허용 인증 방식 : authorization_code, password, refresh_token, client_credentials
* Client 에서 소셜 로그인으로 유입된 사용자가 해당서버에서 추가적인 인증을 받을 수 있도록 AuthenticationProvider 를 커스텀 하였습니다

<br/> 

--------------------------------

## 1. 인증토큰 발급 API

- 입력받은 정보를 기반으로 암호화된 인증 토큰을 리턴합니다.
- 발급받은 토큰은 리소스 서버에 접근할 때 사용됩니다.

<br>

### A. 공통 요청 정보

| Path   | /oauth/token |
|--------|--------------|
| Method | POST         |

| header name   | header value                           |
|---------------|----------------------------------------|
| Content-Type  | application/x-www-form-urlencoded      |
| Authorization | Basic Base64Enc(clientId:clientSecret) |

<br>

### B. 입력 파라미터

password 방식 요청 파라미터

| name       | type          | desc          |
|------------|---------------|---------------|
| grant_type | String        | password      |
| username   | String        | 사용자 계정        |
| password   | String        | 사용자 비밀번호      |

<br>

소셜로그인 추가 인증을 위한 요청 파라미터 (Custom)

| name         | type          | desc         |
|--------------|---------------|--------------|
| grant_type   | String        | password     |
| username     | String        | 사용자 계정       |
| snsSync      | String        | 연동 SNS       |
| snsSecretKey | String        | 연동 SNS 시크릿키  |

<br>

refresh_token 방식 요청 파라미터

| name         | type         | desc               |
|--------------|--------------|--------------------|
| grant_type   | String       | refresh_token      |
| refresh_token | String       | 발급받은 refresh token |

<br>

client_credentials 방식 요청 파라미터

| name          | type          | desc          |
|---------------|---------------|---------------|
| grant_type | String        | client_credentials  |

<br>

Authorization_code 방식 요청 파라미터

| name         | type          | desc                 |
|--------------|---------------|----------------------|
| grant_type   | String        | authorization_code   |
| redirect_uri | String        | 리다이렉트 주소             |
| code         | String        | 인증 코드                |

<br>

### C. 응답 결과

```json
// status 200
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2ODc4NTk3NzIsInVzZXJfbmFtZSI6InVzZXIiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoickVDTkxDelA4ZDh3blZ5R0xLUHZZTGRfSXdBIiwiY2xpZW50X2lkIjoib2F1dGgyLXRlc3QiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXX0.p3VaTDpxpLxzrfSYwl1HZvjZrprhMh-QAqlOaL30oZI",
  "token_type": "bearer",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1c2VyIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sImF0aSI6InJFQ05MQ3pQOGQ4d25WeUdMS1B2WUxkX0l3QSIsImV4cCI6MTY4Nzg2MzA3MiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6ImRScnFVSDRpdkY5UjZYaEVOckUzSWJQT2NZayIsImNsaWVudF9pZCI6Im9hdXRoMi10ZXN0In0.ppBfxB7GxJdXxJBh1WuHWFMOcYxMrs2xG3vAbvfXyzA",
  "expires_in": 299,
  "scope": "read write",
  "jti": "rECNLCzP8d8wnVyGLKPvYLd_IwA"
}
```

```json
// status 400
{
  "error": "invalid_grant",
  "error_description": "Invalid password"
}
```

<br>

-----------------

<br>

## 2. 사용자 정보 조회 API

- 인증 토큰으로 사용자 정보 (디코딩된 토큰) 를 응답 받습니다
- 해당 엔트포인트는 client server 에서 인증토큰을 받은 후 내부 시큐리티 인증 처리를 할 때 사용합니다.

<br/> 

### A. 요청 정보

| Path   | /userinfo |
|--------|-----------|
| Method | GET       |

| parameter name | parameter value              |
|----------------|------------------------------|
| Authorization  | Bearer {Access token String} |

<br />

### B. 응답 결과

```json
// status 200
{
  "exp": 1687737217,
  "user_name": "user",
  "authorities": [
    "ROLE_USER"
  ],
  "jti": "bEV5sU-l5zc4f-V0OiDdqTmJFhQ",
  "client_id": "oauth2-test",
  "scope": [
    "read",
    "write"
  ]
}
```

<br>

-----------------

<br>

## 3. SNS 연동 체크 API

### A. 요청 정보

| Path   | /registerCheck |
|--------|----------------|
| Method | POST           |

| header name   | header value      |
|---------------|-------------------|
| Content-Type  | application/json  |

| parameter name | parameter value   |
|----------------|-------------------|
| username       | 가입한 유저 id         |

<br>

### B. 응답 결과

```json
// 등록된 사용자가 아닌 경우
{
  "result": "N",
  "message": "not Register"
}
```

```json
// sns 연동 계정이 아닌 경우
{
  "result": "N",
  "message": "not snsUser"
}
```

```json
// sns 연동 계정인 경우 (google 연동 계정)
{
  "result": "Y",
  "message": "google"
}
```

<br />


-----------------

<br>

## 4. 사용자 등록 API

### A. 요청 정보

| Path   | /registerCheck |
|--------|----------------|
| Method | POST           |

| header name   | header value      |
|---------------|-------------------|
| Content-Type  | application/json  |

| parameter name | parameter type  | 필수값 유무     | desc                                   |
|----------------|-----------------|------------|----------------------------------------|
| username       | String          | Y          | 사용자 계정                                 |
| password       | String          | Y          | 사용자 비밀번호                               |
| role           | String          | Y          | 사용자 권한 (ROLE_USER or ROLE_ADMIN)       |
| snsSync        | String          | N          | 연동된 SNS 서버명 (google or kakao or naver) |
| snsSecret      | String          | N          | 연동된 SNS 서버에서 가져온 secretKey             |

<br>

### B. 응답 정보

```json
// 이미 가입된 계정인 경우
{
  "result": "N",
  "message": "이미 가입된 계정입니다"
}
```

```json
// 가입에 성공한 경우
{
  "result": "Y",
  "message": "사용자 아이디"
}
```

<br>
