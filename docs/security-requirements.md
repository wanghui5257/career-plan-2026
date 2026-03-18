# 安全要求与 P0 修复总结

## 目的

总结 Phase 2 中发现的安全问题及修复方案，确保生产环境安全，防止类似问题再次发生。

## 适用范围

- 后端开发人员
- 安全审计人员
- 代码审查人员

## P0 安全漏洞总结

### 漏洞 1: 未授权访问返回 200

**问题描述**：
- 用户资料更新 API (`PUT /api/v1/user/profile`) 在未提供 Token 时返回 200
- 攻击者可以无需登录即可修改任意用户资料

**风险等级**：🔴 **P0 - 严重**

**根本原因**：
```java
// ❌ 错误代码
@PutMapping("/profile")
public ApiResponse<UserProfileResponse> updateProfile(
        @RequestBody UserProfileUpdateRequest request) {
    // 没有检查用户是否登录！
    UserProfileResponse profile = userService.updateProfile(userId, request);
    return ApiResponse.success("资料更新成功", profile);
}
```

**修复方案**：
```java
// ✅ 正确代码
@PutMapping("/profile")
public ApiResponse<UserProfileResponse> updateProfile(
        @RequestBody UserProfileUpdateRequest request,
        HttpServletRequest httpRequest) {
    try {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未授权访问");
        }
        UserProfileResponse profile = userService.updateProfile(userId, request);
        return ApiResponse.success("资料更新成功", profile);
    } catch (Exception e) {
        return ApiResponse.error(500, "服务器内部错误");
    }
}
```

**验证方法**：
```bash
# 无 Token 访问，应返回 401
curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/profile \
  -H "Content-Type: application/json" \
  -d '{"name":"test"}'
# 期望：{"code":401,"message":"未授权访问"}
```

**影响范围**：
- `PUT /api/v1/user/profile` - 用户资料更新
- `PUT /api/v1/user/password` - 密码修改
- `GET /api/v1/user/{userId}/profile` - 顾问查看用户资料
- `POST /api/v1/tasks/{taskId}/confirm` - 任务确认

**修复状态**：✅ **已完成**

---

### 漏洞 2: 异常处理不当导致服务崩溃

**问题描述**：
- 未捕获的异常导致 Java 进程崩溃
- 服务返回 502 Bad Gateway
- 需要手动重启服务

**风险等级**：🔴 **P0 - 严重**

**根本原因**：
```java
// ❌ 错误代码
public void changePassword(Long userId, String oldPassword, String newPassword) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("用户不存在"));
    // 没有 try-catch，异常直接抛出导致服务崩溃
    userService.changePassword(userId, oldPassword, newPassword);
}
```

**修复方案**：
```java
// ✅ 正确代码：全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("业务异常：{}", e.getMessage());
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

// ✅ 正确代码：Service 层异常处理
public void changePassword(Long userId, String oldPassword, String newPassword) {
    try {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        // ...
    } catch (RuntimeException e) {
        throw e;  // 业务异常直接抛出，由 GlobalExceptionHandler 处理
    } catch (Exception e) {
        log.error("修改密码异常：{}", e.getMessage(), e);
        throw new RuntimeException("密码修改失败，请稍后重试");
    }
}
```

**验证方法**：
```bash
# 旧密码错误，应返回 400 而不是 500
curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"oldPassword":"wrong","newPassword":"NewPass123"}'
# 期望：{"code":400,"message":"旧密码错误"}
```

**修复状态**：✅ **已完成**

---

## 安全要求清单

### 1. 认证授权

| API 类型 | 要求 | 验证方法 |
|---------|------|---------|
| 需要登录的 API | 必须检查 Token | 无 Token 访问返回 401 |
| 权限控制 API | 必须检查角色 | 无权访问返回 403 |
| 公开 API | 无需认证 | 健康检查、登录 |

**必须验证的 API**：
- [ ] `GET /api/v1/user/profile` - 获取当前用户资料
- [ ] `PUT /api/v1/user/profile` - 更新用户资料
- [ ] `PUT /api/v1/user/password` - 修改密码
- [ ] `GET /api/v1/user/{userId}/profile` - 顾问查看用户资料
- [ ] `POST /api/v1/tasks/{taskId}/confirm` - 任务确认
- [ ] `GET /api/v1/tasks` - 获取任务列表
- [ ] `GET /api/v1/plans` - 获取计划列表

### 2. 密码安全

| 要求 | 说明 | 验证方法 |
|------|------|---------|
| 加密存储 | 必须使用 BCrypt | 数据库中密码为哈希值 |
| 强度验证 | 至少 8 位，包含大写字母、小写字母、数字 | 尝试弱密码被拒绝 |
| 长度上限 | 最多 100 字符 | 尝试超长密码被拒绝 |
| 旧密码验证 | 修改密码必须验证旧密码 | 旧密码错误返回 400 |

**密码规则**（选项 A）：
```
✅ 至少 8 个字符
✅ 包含大写字母 (A-Z)
✅ 包含小写字母 (a-z)
✅ 包含数字 (0-9)
❌ 特殊字符（可选）
```

### 3. 数据安全

| 要求 | 说明 | 验证方法 |
|------|------|---------|
| 输入验证 | 所有输入字段必须验证长度和格式 | 尝试超长/非法输入被拒绝 |
| SQL 注入防护 | 使用 JPA/Hibernate，禁止拼接 SQL | 代码审查确认 |
| XSS 防护 | 返回数据必须转义 | 尝试注入脚本被过滤 |
| 敏感信息保护 | 日志中不能记录明文密码、完整 Token | 检查日志文件 |

### 4. 异常处理

| 异常类型 | HTTP 状态码 | 说明 |
|---------|-----------|------|
| 未授权访问 | 401 | 未登录或 Token 无效 |
| 权限不足 | 403 | 已登录但角色权限不足 |
| 资源不存在 | 404 | 请求的资源不存在 |
| 业务异常 | 400 | 参数错误、验证失败 |
| 服务器错误 | 500 | 未捕获的异常（兜底） |

**禁止**：
- ❌ 业务异常返回 500
- ❌ 未授权访问返回 200
- ❌ 错误信息暴露内部实现细节

### 5. JWT Token 安全

**必须包含的字段**：
```json
{
  "username": "admin",
  "userId": 1,
  "roles": ["ADMIN"],
  "sub": "admin",
  "iat": 1773850837,
  "exp": 1773937237
}
```

**安全要求**：
- [ ] JWT Secret 必须从环境变量或配置文件获取
- [ ] Token 必须有过期时间
- [ ] Token 必须包含用户身份标识
- [ ] 禁止在日志中记录完整 Token

## 安全检查清单

### 代码审查

- [ ] 所有需要认证的 API 是否检查了 Token
- [ ] 未授权访问是否返回 401
- [ ] 权限不足是否返回 403
- [ ] 是否有全局异常处理器
- [ ] 业务异常是否返回 400 而不是 500
- [ ] 密码是否使用 BCrypt 加密
- [ ] 日志中是否有敏感信息
- [ ] 输入验证是否充分

### 部署前验证

- [ ] Staging 环境安全测试通过
- [ ] 所有认证 API 返回正确的 401
- [ ] 异常处理正常（不崩溃）
- [ ] 日志配置正确
- [ ] JWT Secret 配置正确

### 生产环境验证

- [ ] HTTPS 已启用
- [ ] Security 已启用
- [ ] 数据库连接使用生产配置
- [ ] 监控告警已配置
- [ ] 备份策略已实施

## 安全测试用例

### 未授权访问测试

```bash
# 1. 用户资料更新（无 Token）
curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/profile \
  -H "Content-Type: application/json" \
  -d '{"name":"test"}'
# 期望：401 未授权

# 2. 密码修改（无 Token）
curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/password \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"123456","newPassword":"NewPass123"}'
# 期望：401 未授权

# 3. 任务确认（无 Token）
curl -X POST https://staging.plan.shujuyunxiang.com/back-server/api/v1/tasks/1/confirm \
  -H "Content-Type: application/json" \
  -d '{"status":"completed"}'
# 期望：401 未授权
```

### 异常处理测试

```bash
# 1. 旧密码错误（应返回 400）
curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"oldPassword":"wrong","newPassword":"NewPass123"}'
# 期望：400 旧密码错误

# 2. 参数验证失败（应返回 400）
curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"name":"a very long name that exceeds the maximum length limit..."}'
# 期望：400 参数验证失败
```

## 修订历史

| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| 1.0 | 2026-03-19 | career-worker | 初始版本（基于 Phase 2 P0 修复经验） |
