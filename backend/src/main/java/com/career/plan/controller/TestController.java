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

    @GetMapping("/hash")
    public Map<String, Object> generateHash(@RequestParam(defaultValue = "admin123") String password) {
        Map<String, Object> result = new HashMap<>();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(password);
        result.put("password", password);
        result.put("hash", hash);
        result.put("sql", "UPDATE users SET password = '" + hash + "' WHERE username = 'admin';");
        return result;
    }
    
    @GetMapping("/verify")
    public Map<String, Object> verifyPassword(@RequestParam String password, @RequestParam String hash) {
        Map<String, Object> result = new HashMap<>();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(password, hash);
        result.put("password", password);
        result.put("hash", hash);
        result.put("matches", matches);
        return result;
    }
}
