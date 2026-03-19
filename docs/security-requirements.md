# 安全要求 (Security Requirements)

## 目的
确保系统安全性，防止未授权访问和数据泄露。

## 适用范围
- 所有后端 API
- 用户认证与授权
- 敏感数据处理

---

## P0 安全修复总结

### 问题背景
Phase 2 发现未授权访问返回 200（安全漏洞），攻击者可修改他人资料。

### 修复内容
所有未授权访问必须返回 **401**，不能返回 200 或 403。

### 验证用例
| 用例 | 端点 | 期望 | 状态 |
|------|------|------|------|
| UPD-005 | PUT /api/v1/user/profile | 401 | ✅ 已修复 |
| PWD-008 | POST /api/v1/user/password | 401 | ✅ 已修复 |
| PROF-004 | GET /api/v1/user/profile | 401 | ✅ 已修复 |
| CONF-006 | POST /api/v1/task/confirm | 401 | ✅ 已修复 |

---

## 认证要求

### 1. JWT Token 规范
```json
{
  "userId": 1,
  "username": "admin",
  "roles": ["ADMIN"],
  "sub": "admin",
  "iat": 1773837784,
  "exp": 1773924184
}
```

**必须包含字段**：
- `userId`: 用户 ID（用于数据权限）
- `username`: 用户名
- `roles`: 角色列表
- `exp`: 过期时间

### 2. Token 生成
```java
// ✅ 正确：包含 userId
Map<String, Object> claims = new HashMap<>();
claims.put("userId", user.getId());  // ← 必须
claims.put("roles", roles);
claims.put("username", user.getUsername());
jwtTokenProvider.generateToken(claims, user.getUsername());

// ❌ 错误：缺少 userId
Map<String, Object> claims = new HashMap<>();
claims.put("roles", roles);  // ← 缺少 userId
jwtTokenProvider.generateToken(claims, user.getUsername());
```

### 3. Token 验证
```java
// ✅ 正确：使用 @AuthenticationPrincipal
@GetMapping("/profile")
public ApiResponse<?> getUserProfile(
    @AuthenticationPrincipal UserDetails userDetails
) {
    if (userDetails == null) {
        return ApiResponse.error(401, "未授权访问");
    }
    // ...
}
```

---

## 授权要求

### 1. 未授权访问处理
```java
// ✅ 正确：返回 401
if (userDetails == null) {
    return ApiResponse.error(401, "未授权访问");
}

// ❌ 错误：返回 200
if (userDetails == null) {
    return ApiResponse.success(...);  // 安全漏洞！
}

// ❌ 错误：返回 403
if (userDetails == null) {
    return ApiResponse.error(403, "未授权访问");  // 应返回 401
}
```

### 2. 角色权限验证
```java
// ✅ 正确：验证角色
String userRole = userDetails.getAuthorities().stream()
    .findFirst().get().getAuthority();
if ("顾问".equals(userRole)) {
    return ApiResponse.error(403, "顾问无权限");
}
```

### 3. 数据权限验证
```java
// ✅ 正确：验证数据归属
if (!task.getAssignedUserId().equals(currentUserId)) {
    return ApiResponse.error(403, "无权确认此任务");
}
```

---

## 密码安全

### 1. 密码存储
- 使用 bcrypt 加密存储
- 禁止明文存储
- 禁止使用弱哈希（MD5/SHA1）

```java
// ✅ 正确：使用 bcrypt
String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

// ❌ 错误：明文存储
String storedPassword = password;

// ❌ 错误：弱哈希
String hashedPassword = MD5(password);
```

### 2. 密码规则
| 规则 | 要求 | 验证 |
|------|------|------|
| 最小长度 | 8 字符 | ✅ 已实现 |
| 最大长度 | 100 字符 | ⏳ 待实现 |
| 大写字母 | 必须 | ✅ 已实现 |
| 小写字母 | 必须 | ✅ 已实现 |
| 数字 | 必须 | ✅ 已实现 |
| 特殊字符 | 不要求 | ✅ 已确认 |

### 3. 密码修改
- 必须验证旧密码
- 新密码不能与旧密码相同
- 修改后立即生效

```java
// ✅ 正确：验证旧密码
if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
    return ApiResponse.error(400, "旧密码错误");
}
```

---

## 异常处理安全

### 1. 统一异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
        IllegalArgumentException ex
    ) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(400, ex.getMessage()));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(
        BadCredentialsException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(401, "用户名或密码错误"));
    }
}
```

### 2. 禁止泄露敏感信息
```java
// ✅ 正确：通用错误消息
return ApiResponse.error(500, "服务器内部错误");

// ❌ 错误：泄露堆栈信息
return ApiResponse.error(500, ex.getStackTrace());

// ❌ 错误：泄露数据库信息
return ApiResponse.error(500, "SQL Error: " + ex.getMessage());
```

---

## 安全测试

### 1. 未授权访问测试
```bash
# 测试用户资料 API（无 Token）
curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/profile \
  -H "Content-Type: application/json" \
  -d '{"name":"test"}'
# 期望：{"code":401,"message":"未授权访问"}
```

### 2. 权限提升测试
```bash
# 普通用户尝试访问管理员 API
curl -X GET https://staging.plan.shujuyunxiang.com/back-server/api/v1/admin/users \
  -H "Authorization: Bearer $USER_TOKEN"
# 期望：{"code":403,"message":"无权限"}
```

### 3. 数据越权测试
```bash
# 用户 A 尝试修改用户 B 的资料
curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/profile \
  -H "Authorization: Bearer $USER_A_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "name":"hacked"}'
# 期望：{"code":403,"message":"无权修改他人资料"}
```

---

## 安全检查清单

### 开发阶段
- [ ] 所有 API 已添加认证验证
- [ ] 未授权访问返回 401
- [ ] JWT Token 包含 userId
- [ ] 密码使用 bcrypt 加密
- [ ] 异常处理不泄露敏感信息

### 测试阶段
- [ ] 未授权访问测试通过（4/4）
- [ ] 权限提升测试通过
- [ ] 数据越权测试通过
- [ ] 密码规则测试通过

### 部署阶段
- [ ] 生产环境启用 HTTPS
- [ ] JWT Secret 已配置
- [ ] 数据库密码已加密
- [ ] 敏感配置已移除

---

## 修订历史
| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| 1.0 | 2026-03-19 | career-plan-team | Phase 2 初始版本 |
