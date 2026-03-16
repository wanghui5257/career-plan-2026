package com.career.plan.controller;

import com.career.plan.dto.LoginRequest;
import com.career.plan.dto.LoginResponse;
import com.career.plan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword());
    }

    @GetMapping("/test")
    public String test() {
        return "Auth API is working!";
    }
}
