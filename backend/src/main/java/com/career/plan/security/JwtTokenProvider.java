package com.career.plan.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:defaultSecretKey}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        // 尝试从 claims 中获取 userId
        Object userIdObj = claims.get("userId");
        if (userIdObj != null) {
            return Long.valueOf(userIdObj.toString());
        }
        // 如果没有 userId，返回 null，由调用者处理
        return null;
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        
        // 尝试 "roles" 字段（复数，List 格式）
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List) {
            return (List<String>) rolesObj;
        }
        
        // 尝试 "role" 字段（单数，String 格式）- 兼容旧版本 Token
        Object roleObj = claims.get("role");
        if (roleObj instanceof String) {
            return List.of((String) roleObj);
        }
        
        return List.of();
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
    }

    public String generateToken(String username, Long userId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        claims.put("roles", roles);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact();
    }

    public Boolean validateToken(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return !expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
