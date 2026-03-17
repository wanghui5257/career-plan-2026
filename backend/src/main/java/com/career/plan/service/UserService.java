package com.career.plan.service;

import com.career.plan.dto.LoginResponse;
import com.career.plan.entity.User;
import com.career.plan.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret:defaultSecretKey}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(String username, String password) {
        log.info("=== 登录请求 ===");
        log.info("用户名：{}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.error("用户不存在：{}", username);
                return new RuntimeException("用户不存在");
            });

        log.info("找到用户：{}", user.getUsername());
        log.info("数据库密码哈希：{}", user.getPassword());
        log.info("密码哈希前缀：{}", user.getPassword().substring(0, 20));
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        log.info("密码验证结果：{}", matches);
        
        if (!matches) {
            log.error("密码错误 - 输入密码：{}, 数据库哈希：{}", password, user.getPassword());
            throw new RuntimeException("密码错误");
        }

        log.info("密码验证通过，生成 Token...");
        String token = generateToken(user);
        log.info("Token 生成成功");
        
        // 提取用户角色
        String[] roles = new String[]{};
        if (user.getRole() != null) {
            roles = new String[]{user.getRole().getName()};
        }
        
        return new LoginResponse(200, "登录成功", token, jwtExpiration, roles);
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        // 添加 roles 字段（包含角色名称列表）
        if (user.getRole() != null) {
            claims.put("roles", new String[]{user.getRole().getName()});
        }

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact();
    }
}
