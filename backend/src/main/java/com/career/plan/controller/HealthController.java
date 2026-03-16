package com.career.plan.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {
    
    @GetMapping("/health")
    public String health() {
        return "{\"status\": \"UP\", \"message\": \"Career Plan API is running!\"}";
    }
    
    @GetMapping("/")
    public String root() {
        return "Welcome to Career Plan 2026 API";
    }
}
