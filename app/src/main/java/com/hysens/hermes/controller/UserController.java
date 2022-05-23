package com.hysens.hermes.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @PostMapping(value = "/register")
    public void register() {

    }

    @PostMapping("/login")
    public void login() {

    }

}
