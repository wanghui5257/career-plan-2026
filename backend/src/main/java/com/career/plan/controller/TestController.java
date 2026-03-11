package com.career.plan.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = "*")
public class TestController {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/password")
    public Map<String, Object> testPassword() {
        Map<String, Object> result = new HashMap<>();
        
        String plainPassword = "admin123";
        String hashedPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        boolean matches = encoder.matches(plainPassword, hashedPassword);
        
        result.put("plainPassword", plainPassword);
        result.put("hashedPassword", hashedPassword);
        result.put("matches", matches);
        result.put("encoder", "BCrypt");
        
        return result;
    }
    
    @GetMapping("/bcrypt")
    public Map<String, Object> generateBcrypt(@RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        result.put("password", password);
        result.put("bcrypt", encoder.encode(password));
        return result;
    }
}
