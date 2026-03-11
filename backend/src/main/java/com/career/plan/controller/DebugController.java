package com.career.plan.controller;

import com.career.plan.entity.User;
import com.career.plan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 直接验证数据库中的密码
     * 用法：curl "http://localhost:9999/api/v1/debug/verify-password?username=admin&password=admin123"
     */
    @GetMapping("/verify-password")
    public Map<String, Object> verifyDatabasePassword(
            @RequestParam String username,
            @RequestParam String password) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 从数据库查询用户
            Optional<User> userOpt = userRepository.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                result.put("success", false);
                result.put("error", "用户不存在");
                result.put("username", username);
                return result;
            }
            
            User user = userOpt.get();
            String storedHash = user.getPassword();
            
            // 2. 显示数据库中的哈希值
            result.put("success", true);
            result.put("username", username);
            result.put("storedHash", storedHash);
            result.put("storedHashLength", storedHash.length());
            result.put("storedHashPrefix", storedHash.substring(0, Math.min(30, storedHash.length())));
            
            // 3. 验证密码
            boolean matches = encoder.matches(password, storedHash);
            result.put("inputPassword", password);
            result.put("passwordMatches", matches);
            
            // 4. 生成一个新的哈希用于对比
            String newHash = encoder.encode(password);
            result.put("newHash", newHash);
            result.put("newHashLength", newHash.length());
            result.put("newHashPrefix", newHash.substring(0, Math.min(30, newHash.length())));
            
            // 5. 验证新哈希
            boolean newMatches = encoder.matches(password, newHash);
            result.put("newHashMatches", newMatches);
            
            // 6. 分析哈希格式
            result.put("hashAnalysis", analyzeHash(storedHash));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 分析哈希值格式
     */
    private Map<String, Object> analyzeHash(String hash) {
        Map<String, Object> analysis = new HashMap<>();
        
        analysis.put("hash", hash);
        analysis.put("length", hash.length());
        
        if (hash.startsWith("$2a$")) {
            analysis.put("format", "BCrypt ($2a$)");
            analysis.put("valid", true);
            
            if (hash.length() >= 7) {
                String cost = hash.substring(4, 7);
                analysis.put("costFactor", cost);
            }
            
            if (hash.length() == 60) {
                analysis.put("lengthValid", true);
            } else {
                analysis.put("lengthValid", false);
                analysis.put("expectedLength", 60);
            }
            
        } else if (hash.startsWith("$2")) {
            analysis.put("format", "BCrypt (other variant)");
            analysis.put("valid", true);
        } else {
            analysis.put("format", "Unknown (not BCrypt)");
            analysis.put("valid", false);
        }
        
        return analysis;
    }
    
    /**
     * 生成 BCrypt 哈希
     * 用法：curl "http://localhost:9999/api/v1/debug/generate-hash?password=admin123"
     */
    @GetMapping("/generate-hash")
    public Map<String, Object> generateHash(@RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(password);
        
        result.put("password", password);
        result.put("hash", hash);
        result.put("hashLength", hash.length());
        result.put("sql", "UPDATE users SET password = '" + hash + "' WHERE username = 'admin';");
        
        return result;
    }
}
