# 职业发展计划系统 - API 使用指南

## 📋 概述

本指南提供职业发展计划系统后端 API 的完整使用说明。

**服务地址**: `http://47.115.63.159:9999/back-server`

**API 版本**: v1

**基础路径**: `/api/v1`

---

## 🔐 认证说明

### 认证方式

系统采用 JWT (JSON Web Token) 进行身份认证。

### 获取 Token

**接口**: `POST /api/v1/auth/login`

**请求示例**:
```bash
curl -X POST http://47.115.63.159:9999/back-server/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com"
    }
  }
}
```

### 使用 Token

在所有需要认证的接口中，在请求头中添加：

```
Authorization: Bearer <your_token>
```

**示例**:
```bash
curl -X GET http://47.115.63.159:9999/back-server/api/v1/tasks \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 📚 API 接口列表

### 1. 认证接口 (Auth)

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| POST | `/api/v1/auth/login` | 用户登录 | ❌ |
| POST | `/api/v1/auth/logout` | 用户登出 | ✅ |
| POST | `/api/v1/auth/register` | 用户注册 | ❌ |
| POST | `/api/v1/auth/refresh` | 刷新 Token | ✅ |

---

### 2. 任务接口 (Tasks)

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | `/api/v1/tasks` | 获取任务列表 | ✅ |
| GET | `/api/v1/tasks/{id}` | 获取单个任务 | ✅ |
| POST | `/api/v1/tasks` | 创建新任务 | ✅ |
| PUT | `/api/v1/tasks/{id}` | 更新任务 | ✅ |
| DELETE | `/api/v1/tasks/{id}` | 删除任务 | ✅ |
| GET | `/api/v1/tasks/user/{userId}` | 按用户查询任务 | ✅ |
| GET | `/api/v1/tasks/status/{status}` | 按状态查询任务 | ✅ |

#### 任务数据结构

```json
{
  "id": 1,
  "title": "任务标题",
  "description": "任务描述",
  "status": "PENDING",  // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
  "priority": "MEDIUM", // LOW, MEDIUM, HIGH, URGENT
  "userId": 1,
  "createdAt": "2026-03-16T10:00:00Z",
  "updatedAt": "2026-03-16T10:00:00Z"
}
```

#### 创建任务示例

```bash
curl -X POST http://47.115.63.159:9999/back-server/api/v1/tasks \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "完成前端开发",
    "description": "实现用户界面和交互功能",
    "status": "PENDING",
    "priority": "HIGH",
    "userId": 1
  }'
```

---

### 3. 进度报告接口 (Progress Reports)

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | `/api/v1/progress-reports` | 获取进度报告列表 | ✅ |
| GET | `/api/v1/progress-reports/{id}` | 获取单个报告 | ✅ |
| POST | `/api/v1/progress-reports` | 创建新报告 | ✅ |
| PUT | `/api/v1/progress-reports/{id}` | 更新报告 | ✅ |
| DELETE | `/api/v1/progress-reports/{id}` | 删除报告 | ✅ |
| GET | `/api/v1/progress-reports/user/{userId}` | 按用户查询 | ✅ |
| GET | `/api/v1/progress-reports/task/{taskId}` | 按任务查询 | ✅ |

#### 查询参数

| 参数 | 类型 | 描述 |
|------|------|------|
| startDate | String | 开始日期 (ISO 格式) |
| endDate | String | 结束日期 (ISO 格式) |
| userId | Long | 用户 ID |
| taskId | Long | 任务 ID |

#### 进度报告数据结构

```json
{
  "id": 1,
  "userId": 1,
  "taskId": 1,
  "content": "今日完成了用户登录功能的开发",
  "workHours": 2.5,
  "reportDate": "2026-03-16",
  "createdAt": "2026-03-16T18:00:00Z"
}
```

#### 创建进度报告示例

```bash
curl -X POST http://47.115.63.159:9999/back-server/api/v1/progress-reports \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "taskId": 1,
    "content": "完成了 API 接口开发和单元测试",
    "workHours": 4.0,
    "reportDate": "2026-03-16"
  }'
```

---

### 4. 用户接口 (Users)

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | `/api/v1/users/{id}` | 获取用户信息 | ✅ |
| PUT | `/api/v1/users/{id}` | 更新用户信息 | ✅ |
| PUT | `/api/v1/users/{id}/password` | 修改密码 | ✅ |

---

### 5. 健康检查接口 (Health)

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | `/actuator/health` | 服务健康状态 | ❌ |
| GET | `/actuator/info` | 应用信息 | ❌ |
| GET | `/actuator/metrics` | 性能指标 | ❌ |

---

## 🔧 Swagger UI

### 访问地址

- **Swagger UI**: http://47.115.63.159:9999/back-server/swagger-ui.html
- **API Docs (OpenAPI JSON)**: http://47.115.63.159:9999/back-server/api-docs
- **Swagger UI (新版)**: http://47.115.63.159:9999/back-server/swagger-ui/index.html

### 使用 Swagger UI 测试 API

1. 打开浏览器访问 Swagger UI 地址
2. 点击Authorize按钮
3. 输入 Token (格式：`Bearer <your_token>`)
4. 展开需要测试的接口
5. 填写请求参数
6. 点击"Execute"执行请求
7. 查看响应结果

---

## 📊 响应格式

### 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": "2026-03-16T10:00:00Z"
}
```

### 错误响应

```json
{
  "code": 400,
  "message": "错误描述",
  "errors": [
    {
      "field": "username",
      "message": "用户名不能为空"
    }
  ],
  "timestamp": "2026-03-16T10:00:00Z"
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 (Token 无效或过期) |
| 403 | 禁止访问 (权限不足) |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 🧪 测试脚本

项目提供了以下测试脚本用于验证 API 功能：

### 1. 健康检查脚本

```bash
cd backend/scripts
./production-health-check.sh
```

### 2. API 接口测试脚本

```bash
cd backend/scripts
./production-api-test.sh
```

### 3. 数据库检查脚本

```bash
cd backend/scripts
./production-db-check.sh
```

### 4. 性能测试脚本

```bash
cd backend/scripts
./production-performance-test.sh
```

---

## 📝 最佳实践

### 1. Token 管理

- Token 有效期为 24 小时
- 建议在 Token 过期前 1 小时刷新
- 不要在客户端硬编码 Token
- 使用环境变量或配置文件存储 Token

### 2. 错误处理

- 始终检查响应的 `code` 字段
- 处理 401 错误时，尝试刷新 Token 或重新登录
- 处理 400 错误时，检查 `errors` 数组获取详细错误信息

### 3. 请求频率

- 建议单个用户请求频率不超过 100 次/分钟
- 批量操作使用批量接口（如有）
- 避免在循环中频繁调用 API

### 4. 数据安全

- 使用 HTTPS (生产环境)
- 不要在 URL 中传递敏感信息
- 定期修改密码

---

## 🔗 相关链接

- **GitHub 仓库**: https://github.com/wanghui5257/career-plan-2026
- **后端代码**: https://github.com/wanghui5257/career-plan-2026/tree/main/backend
- **Swagger UI**: http://47.115.63.159:9999/back-server/swagger-ui.html

---

## 📞 技术支持

如有问题，请联系：

- **项目负责人**: admin
- **开发团队**: backend-dev
- **项目房间**: Project: 职业发展计划 2026

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-16  
**维护者**: backend-dev
