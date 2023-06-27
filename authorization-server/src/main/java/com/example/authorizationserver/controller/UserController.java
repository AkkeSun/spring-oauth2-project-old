package com.example.authorizationserver.controller;

import com.example.authorizationserver.domain.MemberDTO;
import com.example.authorizationserver.domain.ResponseDTO;
import com.example.authorizationserver.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final CustomUserDetailService userDetailService;

    @PostMapping("/user")
    public ResponseEntity<ResponseDTO> register(@RequestBody MemberDTO memberDTO) {
        return ResponseEntity.ok(userDetailService.register(memberDTO));
    }

    @PostMapping("/registerCheck")
    public ResponseEntity<ResponseDTO> registerCheck(@RequestBody MemberDTO memberDTO) {
        return ResponseEntity.ok(userDetailService.registerCheck(memberDTO));
    }

    @GetMapping(value = "/userinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserInfo(
        @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(JwtHelper.decode(token.replace("Bearer ", "")).getClaims());
    }
}
