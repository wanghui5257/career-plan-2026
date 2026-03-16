# 测试用例：用户认证模块

## 用例 ID: AUTH-001

### 用例描述
验证用户登录功能，包括正确凭证登录、错误密码、错误用户名等场景。

### 前置条件
- 后端服务已启动
- 数据库中存在 admin 用户
- 网络连通性正常

### 测试步骤

#### 步骤 1: 正确凭证登录
```bash
curl -X POST https://plan.shujuyunxiang.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

#### 步骤 2: 错误密码登录
```bash
curl -X POST https://plan.shujuyunxiang.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrongpassword"}'
```

#### 步骤 3: 错误用户名登录
```bash
curl -X POST https://plan.shujuyunxiang.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"nonexistent","password":"admin123"}'
```

#### 步骤 4: 空用户名登录
```bash
curl -X POST https://plan.shujuyunxiang.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"","password":"admin123"}'
```

#### 步骤 5: 空密码登录
```bash
curl -X POST https://plan.shujuyunxiang.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":""}'
```

### 预期结果

| 步骤 | 预期状态码 | 预期响应内容 |
|------|------------|--------------|
| 步骤 1 | 200 | `{"code":200,"message":"登录成功","token":"...","expiresIn":86400000}` |
| 步骤 2 | 401 | `{"code":401,"message":"密码错误"}` |
| 步骤 3 | 401 | `{"code":401,"message":"用户不存在"}` |
| 步骤 4 | 400 | `{"code":400,"message":"用户名不能为空"}` 或类似验证错误 |
| 步骤 5 | 400 | `{"code":400,"message":"密码不能为空"}` 或类似验证错误 |

### 实际结果
待执行...

### 状态
⏳ 待执行

---

## 用例 ID: AUTH-002

### 用例描述
验证 JWT Token 的有效性和过期机制。

### 前置条件
- 已成功登录获取 Token
- Token 未过期

### 测试步骤

#### 步骤 1: 使用有效 Token 访问受保护接口
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  # 从登录响应获取
curl -X GET https://plan.shujuyunxiang.com/api/v1/tasks \
  -H "Authorization: Bearer $TOKEN"
```

#### 步骤 2: 使用无效 Token 访问受保护接口
```bash
curl -X GET https://plan.shujuyunxiang.com/api/v1/tasks \
  -H "Authorization: Bearer invalid_token"
```

#### 步骤 3: 无 Token 访问受保护接口
```bash
curl -X GET https://plan.shujuyunxiang.com/api/v1/tasks
```

### 预期结果

| 步骤 | 预期状态码 | 预期响应内容 |
|------|------------|--------------|
| 步骤 1 | 200 | 返回任务列表 JSON |
| 步骤 2 | 401 | `{"code":401,"message":"Token 无效"}` |
| 步骤 3 | 401 | `{"code":401,"message":"未授权"}` |

### 实际结果
待执行...

### 状态
⏳ 待执行

---

## 用例 ID: AUTH-003

### 用例描述
验证健康检查接口可用性。

### 前置条件
- 后端服务已启动

### 测试步骤
```bash
curl -X GET https://plan.shujuyunxiang.com/actuator/health
```

### 预期结果
- 状态码：200
- 响应内容：`{"status":"UP","groups":["liveness","readiness"]}`

### 实际结果
待执行...

### 状态
⏳ 待执行

---

**编写人**: qa-tester  
**编写日期**: 2026-03-16  
**优先级**: P0
