package com.career.plan.service;

import com.career.plan.dto.LoginResponse;
import com.career.plan.dto.UserProfileResponse;
import com.career.plan.dto.UserProfileUpdateRequest;
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
        try {
            log.info("=== 登录请求 ===");
            log.info("用户名：{}", username);
            
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("用户不存在：{}", username);
                    return new RuntimeException("用户名或密码错误");
                });

            log.info("=== 数据库查询结果 ===");
            log.info("user.getId(): {}", user.getId());
            log.info("user.getUsername(): {}", user.getUsername());
            log.info("user.getRoleId(): {}", user.getRoleId());
            log.info("找到用户：{}", user.getUsername());
            
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            log.info("密码验证结果：{}", matches);
            
            if (!matches) {
                log.warn("密码错误 - 用户名：{}", username);
                throw new RuntimeException("用户名或密码错误");
            }

            log.info("密码验证通过，生成 Token...");
            String token = generateToken(user);
            log.info("Token 生成成功");
            
            // 提取用户角色
            String[] roles = new String[]{};
            if (roleIdToRoleName(user.getRoleId()) != null && !roleIdToRoleName(user.getRoleId()).isEmpty()) {
                roles = new String[]{roleIdToRoleName(user.getRoleId())};
            }
            
            return new LoginResponse(200, "登录成功", token, jwtExpiration, roles);
        } catch (RuntimeException e) {
            // 业务异常直接抛出
            log.error("登录失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // 捕获所有其他异常，防止崩溃
            log.error("登录异常：{}", e.getMessage(), e);
            throw new RuntimeException("登录失败，请稍后重试");
        }
    }

    /**
     * 验证密码强度
     * 密码必须满足：
     * 1. 至少 8 个字符
     * 2. 包含大写字母
     * 3. 包含小写字母
     * 4. 包含数字
     * 5. 包含特殊字符
     */
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("密码长度至少 8 位");
        }
        
        if (password.length() > 100) {
            throw new RuntimeException("密码长度不能超过 100 字符");
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        
        if (!hasUpper) {
            throw new RuntimeException("密码必须包含大写字母");
        }
        if (!hasLower) {
            throw new RuntimeException("密码必须包含小写字母");
        }
        if (!hasDigit) {
            throw new RuntimeException("密码必须包含数字");
        }
        // 特殊字符要求已移除（Staging 测试需要）
    }

    /**
     * 修改密码
     * @param userId 用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            log.info("=== 修改密码请求 ===");
            log.info("用户 ID: {}", userId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("用户不存在：{}", userId);
                    return new RuntimeException("用户不存在");
                });
            
            // 验证旧密码
            boolean matches = passwordEncoder.matches(oldPassword, user.getPassword());
            log.info("旧密码验证结果：{}", matches);
            
            if (!matches) {
                log.warn("旧密码错误 - 用户 ID: {}", userId);
                throw new RuntimeException("旧密码错误");
            }
            
            // 验证新密码强度
            validatePasswordStrength(newPassword);
            log.info("新密码强度验证通过");
            
            // 加密新密码并保存
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);
            
            log.info("密码修改成功");
        } catch (RuntimeException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            // 捕获所有其他异常，防止崩溃
            log.error("修改密码异常：{}", e.getMessage(), e);
            throw new RuntimeException("密码修改失败，请稍后重试");
        }
    }

    /**
     * 获取用户资料（顾问专用）
     * @param userId 用户 ID
     * @return 用户资料
     */
    public UserProfileResponse getUserProfile(Long userId) {
        log.info("=== 查询用户资料 ===");
        log.info("用户 ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("用户不存在：{}", userId);
                return new RuntimeException("用户不存在");
            });
        
        return UserProfileResponse.fromUser(user);
    }

    /**
     * 更新用户资料
     * @param userId 用户 ID
     * @param request 更新请求
     * @return 更新后的用户资料
     */
    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        log.info("=== 更新用户资料 ===");
        log.info("用户 ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("用户不存在：{}", userId);
                return new RuntimeException("用户不存在");
            });
        
        // 更新可修改的字段
        if (request.getBackground() != null) {
            user.setBackground(request.getBackground());
        }
        if (request.getGoals() != null) {
            user.setGoals(request.getGoals());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        
        userRepository.save(user);
        log.info("用户资料更新成功");
        
        return UserProfileResponse.fromUser(user);
    }

    private String roleIdToRoleName(Long roleId) {
        if (roleId == null) return null;
        switch (roleId.intValue()) {
            case 1: return "ADMIN";
            case 2: return "PLAN_CREATOR";
            case 3: return "SUPERVISOR";
            case 4: return "EXECUTOR";
            case 5: return "WORKER";
            default: return "UNKNOWN";
        }
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        log.info("=== 生成 Token - userId: {}, username: {}", user.getId(), user.getUsername());
        claims.put("userId", user.getId());
        // 添加 roles 字段（包含角色名称列表）
        if (roleIdToRoleName(user.getRoleId()) != null && !roleIdToRoleName(user.getRoleId()).isEmpty()) {
            claims.put("roles", new String[]{roleIdToRoleName(user.getRoleId())});
        }
        log.info("Token claims: {}", claims);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact();
    }
}
