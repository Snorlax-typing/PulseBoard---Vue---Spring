package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot! 你的第一个接口已经跑起来了 🎉";
    }

    @GetMapping("/hello")
    public String helloName() {
        return "Hello, World! 这是 /hello 接口";
    }
}
