# 代码审查清单 (Code Review Checklist)

## 目的
确保代码质量、安全性和合规性。

## 适用范围
- 后端代码审查
- 前端代码审查
- 测试代码审查

---

## P0 安全审查（必须 100% 通过）

### 1. 未授权访问验证
- [ ] 所有 API 已添加 `@AuthenticationPrincipal` 验证
- [ ] 未授权访问返回 **401**，不是 200 或 403
- [ ] 无授权测试用例已通过（4/4）

**检查点**：
```java
// ✅ 通过
@GetMapping("/profile")
public ApiResponse<?> getUserProfile(
    @AuthenticationPrincipal UserDetails userDetails
) {
    if (userDetails == null) {
        return ApiResponse.error(401, "未授权访问");
    }
}

// ❌ 不通过：缺少验证
@GetMapping("/profile")
public ApiResponse<?> getUserProfile() {
    // 未验证用户身份
}

// ❌ 不通过：返回 200
if (userDetails == null) {
    return ApiResponse.success(...);  // 安全漏洞！
}
```

### 2. JWT Token 验证
- [ ] Token 包含 `userId` 字段
- [ ] `userId` 用于数据权限验证
- [ ] Token 过期时间合理

**检查点**：
```java
// ✅ 通过：包含 userId
claims.put("userId", user.getId());

// ❌ 不通过：缺少 userId
claims.put("roles", roles);  // 缺少 userId
```

---

## 代码质量审查

### 1. 异常处理
- [ ] 使用 `GlobalExceptionHandler` 统一处理
- [ ] 业务异常返回适当状态码（400/401/403/404）
- [ ] 禁止返回 500（除非服务器崩溃）
- [ ] 不泄露敏感信息（堆栈、SQL、配置）

**检查点**：
```java
// ✅ 通过：使用异常处理器
@ExceptionHandler(IllegalArgumentException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ResponseEntity<ApiResponse<?>> handleException(...)

// ❌ 不通过：直接抛出 500
throw new RuntimeException("...");

// ❌ 不通过：泄露敏感信息
return ApiResponse.error(500, "SQL Error: " + ex.getMessage());
```

### 2. 密码安全
- [ ] 使用 bcrypt 加密存储
- [ ] 密码规则已实现（8 位 + 大写 + 小写 + 数字）
- [ ] 旧密码验证已实现
- [ ] 密码长度上限验证（待实现）

**检查点**：
```java
// ✅ 通过：使用 bcrypt
String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

// ✅ 通过：密码规则验证
validatePasswordStrength(newPassword);  // 8 位 + 大写 + 小写 + 数字

// ❌ 不通过：明文存储
String storedPassword = password;
```

### 3. 数据验证
- [ ] 输入参数已验证（长度、格式、范围）
- [ ] 空值处理已实现
- [ ] 资源不存在返回 404
- [ ] 权限验证已实现

**检查点**：
```java
// ✅ 通过：完整验证
if (name == null || name.isEmpty()) {
    return ApiResponse.error(400, "姓名不能为空");
}
if (name.length() > 200) {
    return ApiResponse.error(400, "姓名长度不能超过 200");
}

// ✅ 通过：资源不存在验证
if (user == null) {
    return ApiResponse.error(404, "用户不存在");
}

// ✅ 通过：权限验证
if (!task.getAssignedUserId().equals(currentUserId)) {
    return ApiResponse.error(403, "无权确认此任务");
}
```

---

## 代码规范审查

### 1. 命名规范
- [ ] 类名：PascalCase（`UserService`）
- [ ] 方法名：camelCase（`getUserProfile`）
- [ ] 变量名：camelCase（`userId`）
- [ ] 常量名：UPPER_SNAKE_CASE（`MAX_PASSWORD_LENGTH`）

### 2. 代码结构
- [ ] 方法长度 ≤ 50 行
- [ ] 类长度 ≤ 500 行
- [ ] 单一职责原则
- [ ] DRY（Don't Repeat Yourself）

### 3. 注释规范
- [ ] 公共方法有 JavaDoc
- [ ] 复杂逻辑有注释
- [ ] 注释使用中文
- [ ] 无过时注释

---

## 测试审查

### 1. 单元测试
- [ ] 覆盖率 ≥ 80%
- [ ] 测试用例独立
- [ ] 测试数据隔离
- [ ] 清理机制完善

### 2. API 测试
- [ ] 使用正确的 HTTP 方法
- [ ] 检查 JSON `code` 字段
- [ ] 无授权测试真正无 Token
- [ ] 测试通过率 ≥ 90%

### 3. 测试报告
- [ ] 根因分析完整
- [ ] 失败用例分类清晰
- [ ] 修复建议明确

---

## 部署审查

### 1. 构建验证
- [ ] 前端构建产物大小正常（约 13.32 MiB）
- [ ] 后端 JAR 包大小正常（75-77MB）
- [ ] 构建产物包含修复代码

### 2. 部署验证
- [ ] 健康检查通过
- [ ] 登录 API 通过
- [ ] P0 安全验证通过（4/4）
- [ ] 关键业务功能通过

### 3. 配置验证
- [ ] 使用域名访问（禁止 IP+端口）
- [ ] HTTPS 已启用
- [ ] JWT Secret 已配置
- [ ] 数据库连接正确

---

## 文档审查

### 1. 代码文档
- [ ] README 已更新
- [ ] API 文档已更新
- [ ] 变更日志已编写

### 2. 测试文档
- [ ] 测试报告已生成
- [ ] 测试用例已归档
- [ ] 已知问题已记录

### 3. 部署文档
- [ ] 部署流程已更新
- [ ] 回滚流程已更新
- [ ] 常见问题已记录

---

## 审查流程

### 1. 提交审查
```
1. 开发者完成代码
2. 提交 PR 到 GitHub
3. 填写 PR 描述（变更内容、测试情况）
4. 指派审查人（saas-architect）
```

### 2. 审查执行
```
1. 审查人检查代码
2. 提出审查意见
3. 开发者修复问题
4. 审查人重新审查
5. 审查通过后合并
```

### 3. 审查标准
| 问题级别 | 处理 | 示例 |
|----------|------|------|
| P0 安全 | 必须修复，禁止合并 | 未授权访问返回 200 |
| P1 功能 | 必须修复，禁止合并 | 密码长度上限未验证 |
| P2 规范 | 建议修复，可合并 | 命名不规范 |
| P3 优化 | 可选修复 | 代码重构 |

---

## 审查记录模板

```markdown
# 代码审查报告

## PR 信息
- **PR**: #XX
- **标题**: Phase 2 - 用户中心与顾问功能
- **作者**: XXX
- **审查人**: saas-architect
- **日期**: 2026-03-19

## 审查结果
| 类别 | 状态 | 说明 |
|------|------|------|
| P0 安全 | ✅ 通过 | 4/4 未授权访问测试通过 |
| 代码质量 | ✅ 通过 | 异常处理完整 |
| 测试覆盖 | ✅ 通过 | 覆盖率 85% |
| 文档完整 | ✅ 通过 | API 文档已更新 |

## 审查意见
### 已修复
- [x] P0: 未授权访问返回 401
- [x] P1: 密码规则验证

### 待修复（下一迭代）
- [ ] P1: 密码长度上限验证
- [ ] P2: 支持 "current" 路径参数

## 审查结论
✅ **通过** - 可以合并
```

---

## 修订历史
| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| 1.0 | 2026-03-19 | career-plan-team | Phase 2 初始版本 |
