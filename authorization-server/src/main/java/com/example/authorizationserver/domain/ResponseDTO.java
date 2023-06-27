package com.example.authorizationserver.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDTO {

    private String result;
    private String message;
}
