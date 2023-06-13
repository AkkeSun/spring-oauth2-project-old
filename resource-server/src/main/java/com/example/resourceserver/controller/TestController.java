package com.example.resourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test1")
    public String test1(){
        return "hello user";
    }

    @GetMapping("/test2")
    public String test2(){
        return "hello admin";
    }

    @GetMapping("/test3")
    public String test3(){
        return "hello read users";
    }

    @GetMapping("/test4")
    public String test4(){
        return "hello all users";
    }
}
