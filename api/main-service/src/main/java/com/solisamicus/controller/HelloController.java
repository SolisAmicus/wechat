package com.solisamicus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("m")
public class HelloController {
    @GetMapping("hello")
    public Object hello() {
        return "Hello!";
    }
}
