package com.career.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private int code;
    private String message;
    private String token;
    private long expiresIn;
<<<<<<< HEAD
    private String[] roles;  // 用户角色列表
    
    // 兼容旧版本的构造函数
    public LoginResponse(int code, String message, String token, long expiresIn) {
        this.code = code;
        this.message = message;
        this.token = token;
        this.expiresIn = expiresIn;
        this.roles = new String[]{};
    }
}
