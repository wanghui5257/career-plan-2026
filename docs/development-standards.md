# 开发规范 (Development Standards)

## 目的
确保代码质量、可维护性和团队协作效率。

## 适用范围
- 后端开发 (Spring Boot/Java)
- 前端开发 (React/Vite)
- 测试开发 (Bash/JavaScript)

---

## 后端开发规范

### 1. 代码结构
```
src/main/java/
├── controller/     # REST API 控制器
├── service/        # 业务逻辑层
├── repository/     # 数据访问层
├── model/          # 数据模型
├── dto/            # 数据传输对象
├── config/         # 配置类
└── exception/      # 异常处理
```

### 2. 异常处理
- 使用 `GlobalExceptionHandler` 统一处理异常
- 业务异常返回适当的 HTTP 状态码（400/401/403/404）
- 禁止返回 500 错误（除非服务器崩溃）

```java
// ✅ 正确：使用异常处理器
@ExceptionHandler(IllegalArgumentException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ResponseEntity<ApiResponse<?>> handleException(...)

// ❌ 错误：直接抛出 500
throw new RuntimeException("...");
```

### 3. 安全要求 (P0)
- **未授权访问必须返回 401**，不能返回 200
- 使用 `@AuthenticationPrincipal` 验证用户身份
- JWT Token 必须包含 `userId` 字段

```java
// ✅ 正确：验证用户身份
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

### 4. 密码规则
- 最小长度：8 字符
- 必须包含：大写字母 + 小写字母 + 数字
- 最大长度：100 字符（待实现）
- 特殊字符：不要求

### 5. API 设计规范
- 使用 RESTful 命名
- 统一响应格式：`{"code": 200, "message": "...", "data": {...}}`
- 使用正确的 HTTP 方法（GET/POST/PUT/DELETE）

---

## 前端开发规范

### 1. 组件结构
```
src/
├── components/     # 可复用组件
├── pages/          # 页面组件
├── services/       # API 服务
├── utils/          # 工具函数
└── styles/         # 样式文件
```

### 2. API 调用
- 使用统一的基础 URL 配置
- 所有 API 调用必须处理错误
- 使用环境变量区分环境

```javascript
// ✅ 正确：使用环境变量
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// ❌ 错误：硬编码
const API_BASE_URL = "http://localhost:9997";
```

### 3. 表单验证
- 前端验证 + 后端验证（双重保障）
- 清晰的错误提示
- 密码强度实时反馈

---

## 测试开发规范

### 1. 测试脚本
- 使用正确的 HTTP 方法
- 检查 JSON `code` 字段，不是 HTTP 状态码
- 无授权测试必须真正无 Token

```bash
# ✅ 正确：无 Token 测试
curl -X PUT $BASE_URL/api/v1/user/profile \
  -H "Content-Type: application/json" \
  -d '{"name":"test"}'
# 不应该有 -H "Authorization: Bearer ..."

# ❌ 错误：测试脚本 Bug（已修复）
# 传递空字符串但回退到默认 Token
```

### 2. 测试清理机制
- 每个修改密码用例后立即恢复
- 使用 `trap` 确保清理执行
- 测试前备份原始数据

### 3. 测试数据管理
- 测试任务必须分配给测试账号
- 使用独立的测试数据，不影响生产

---

## 提交规范

### Git Commit Message
```
<类型>: <简短描述>

[可选的详细描述]

[可选的关联 Issue]
```

**类型**：
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `test`: 测试相关
- `refactor`: 代码重构
- `chore`: 构建/工具

**示例**：
```
feat: 实现密码修改功能

- 添加 ChangePasswordForm 组件
- 实现密码强度验证
- 添加旧密码确认逻辑

Closes #123
```

---

## 修订历史
| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| 1.0 | 2026-03-19 | career-plan-team | Phase 2 初始版本 |
