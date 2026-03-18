# 后端开发规范

## 目的

规范后端开发流程，确保代码质量、安全性和可维护性，避免 Phase 2 中遇到的问题重演。

## 适用范围

- 后端开发人员
- 代码审查人员
- 测试人员

## 开发规范

### 1. 代码结构

```
backend/
├── src/main/java/com/career/plan/
│   ├── config/          # 配置类（Security, CORS, Exception Handler）
│   ├── controller/      # REST API 控制器
│   ├── service/         # 业务逻辑层
│   ├── repository/      # 数据访问层
│   ├── entity/          # JPA 实体
│   ├── dto/             # 数据传输对象
│   ├── security/        # 安全相关（JWT, Filter）
│   └── exception/       # 自定义异常
├── src/main/resources/
│   ├── application.yml
│   ├── application-staging.yml
│   └── application-production.yml
└── src/test/            # 测试代码
```

### 2. 异常处理

**必须遵守**：

```java
// ✅ 正确：全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(400, e.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        log.error("未捕获的异常：{}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(500, "服务器内部错误，请稍后重试"));
    }
}

// ✅ 正确：Controller 层异常捕获
@PutMapping("/password")
public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest request) {
    try {
        userService.changePassword(request);
        return ApiResponse.success("密码修改成功", null);
    } catch (RuntimeException e) {
        return ApiResponse.error(400, e.getMessage());
    } catch (Exception e) {
        return ApiResponse.error(500, "服务器内部错误");
    }
}
```

**禁止**：
- ❌ 让异常直接抛出导致服务崩溃
- ❌ 返回 500 错误给业务异常（应返回 400）
- ❌ 在错误信息中暴露内部实现细节

### 3. 安全要求

#### 3.1 认证授权

```java
// ✅ 正确：未授权访问检查
@PutMapping("/profile")
public ApiResponse<UserProfileResponse> updateProfile(
        @RequestBody UserProfileUpdateRequest request,
        HttpServletRequest httpRequest) {
    try {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        // ... 业务逻辑
    }
}
```

**必须验证**：
- 所有需要认证的 API 必须检查 `userId` 或 Token
- 未授权访问必须返回 **401**（不是 200 或 500）
- 权限不足必须返回 **403**

#### 3.2 密码安全

```java
// ✅ 正确：密码验证
private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

public boolean validatePassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
}

// ✅ 正确：密码加密
public String encodePassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
}
```

**必须遵守**：
- 密码必须使用 BCrypt 加密
- 密码强度验证（至少 8 位，包含大写字母、小写字母、数字）
- 密码长度上限 100 字符（防止 DoS 攻击）
- 旧密码验证必须使用 `matches()` 方法

#### 3.3 JWT Token

```java
// ✅ 正确：Token 生成
private String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", user.getUsername());
    claims.put("userId", user.getId());
    claims.put("roles", new String[]{roleIdToRoleName(user.getRoleId())});
    
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(SignatureAlgorithm.HS256, jwtSecret)
        .compact();
}
```

**必须包含**：
- `username`: 用户名
- `userId`: 用户 ID
- `roles`: 角色列表
- `exp`: 过期时间

### 4. 数据验证

```java
// ✅ 正确：字段长度验证
@PutMapping("/profile")
public ApiResponse<UserProfileResponse> updateProfile(
        @RequestBody UserProfileUpdateRequest request) {
    if (request.getName() != null && request.getName().length() > 200) {
        return ApiResponse.error(400, "name 字段长度不能超过 200 字符");
    }
    if (request.getPhone() != null && request.getPhone().length() > 50) {
        return ApiResponse.error(400, "phone 字段长度不能超过 50 字符");
    }
}
```

**必须验证**：
- 所有输入字段长度
- 必填字段非空检查
- 邮箱格式验证
- 手机号格式验证

### 5. 日志规范

```java
// ✅ 正确：日志记录
log.info("=== 登录请求 ===");
log.info("用户名：{}", username);
log.info("密码验证结果：{}", matches);

log.warn("密码错误 - 用户名：{}", username);
log.error("登录异常：{}", e.getMessage(), e);
```

**必须记录**：
- 关键业务操作（登录、修改密码、资料更新）
- 异常情况（包含堆栈）
- 安全相关事件（未授权访问、权限不足）

**禁止记录**：
- ❌ 明文密码
- ❌ 完整 Token
- ❌ 用户敏感信息（身份证号、银行卡号）

### 6. 数据库操作

```java
// ✅ 正确：使用 Optional 处理查询结果
User user = userRepository.findByUsername(username)
    .orElseThrow(() -> new RuntimeException("用户不存在"));

// ✅ 正确：事务管理
@Transactional
public void changePassword(Long userId, String oldPassword, String newPassword) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("用户不存在"));
    // ...
}
```

**必须遵守**：
- 查询结果必须处理空值
- 写操作必须使用 `@Transactional`
- 批量操作必须分批处理

## 操作流程

### 1. 开发前
- [ ] 确认需求
- [ ] 设计 API 接口
- [ ] 确认数据库变更

### 2. 开发中
- [ ] 编写代码
- [ ] 编写单元测试
- [ ] 本地测试

### 3. 提交前
- [ ] 代码自检
- [ ] 运行测试
- [ ] 更新文档

### 4. 提交后
- [ ] 创建 PR
- [ ] 代码审查
- [ ] 合并到主分支

## 验证方法

### 代码审查清单
- [ ] 异常处理是否正确
- [ ] 安全验证是否完整
- [ ] 日志记录是否合规
- [ ] 数据验证是否充分
- [ ] 单元测试是否覆盖

### 自动化测试
- [ ] 单元测试通过率 100%
- [ ] API 测试通过率 90%+
- [ ] 安全测试通过

## 常见问题

### Q1: 为什么未授权访问返回 401 而不是 403？
**A**: 401 表示未认证（需要登录），403 表示认证但权限不足。

### Q2: 密码强度规则是什么？
**A**: 至少 8 位，包含大写字母、小写字母、数字。特殊字符可选。

### Q3: 如何防止服务崩溃？
**A**: 使用 `GlobalExceptionHandler` 捕获所有未处理异常，返回友好错误信息。

## 修订历史

| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| 1.0 | 2026-03-19 | career-worker | 初始版本（基于 Phase 2 经验） |
