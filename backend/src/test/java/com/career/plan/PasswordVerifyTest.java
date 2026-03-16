package com.career.plan;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.jupiter.api.Test;

public class PasswordVerifyTest {

    @Test
    public void testPasswordVerify() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String plainPassword = "admin123";
        String dbHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        // 生成新哈希
        String newHash = encoder.encode(plainPassword);
        System.out.println("=== 新哈希 ===");
        System.out.println("NEW_HASH=" + newHash);
        
        // 验证新哈希
        boolean newVerify = encoder.matches(plainPassword, newHash);
        System.out.println("NEW_VERIFY=" + newVerify);
        
        // 验证数据库哈希
        boolean dbVerify = encoder.matches(plainPassword, dbHash);
        System.out.println("DB_VERIFY=" + dbVerify);
        
        // 输出 SQL
        System.out.println("=== SQL ===");
        System.out.println("UPDATE users SET password = '" + newHash + "' WHERE username = 'admin';");
        
        if (!dbVerify) {
            System.out.println("=== 结论：数据库哈希无效，请执行上面 SQL 更新 ===");
        }
    }
}
