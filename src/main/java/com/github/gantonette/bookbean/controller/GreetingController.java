package com.github.gantonette.bookbean.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
    @GetMapping("/")
    String home() {
        return "Hello World!";
    }
}
