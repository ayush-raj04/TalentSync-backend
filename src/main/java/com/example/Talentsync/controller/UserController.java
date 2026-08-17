package com.example.Talentsync.controller;

import com.example.Talentsync.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/user/profile")
    public User getProfile(Authentication authentication) {

        return (User) authentication.getPrincipal();

    }
}