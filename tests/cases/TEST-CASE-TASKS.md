# 测试用例：任务管理模块

## 用例 ID: TASK-001

### 用例描述
验证获取任务列表功能。

### 前置条件
- 后端服务已启动
- 已获取有效 Token
- 数据库中存在测试任务数据

### 测试步骤
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  # 从登录响应获取
curl -X GET https://plan.shujuyunxiang.com/api/v1/tasks \
  -H "Authorization: Bearer $TOKEN"
```

### 预期结果
- 状态码：200
- 响应内容：JSON 数组，包含任务列表
- 每个任务包含字段：id, title, status, priority, dueDate 等

### 实际结果
待执行...

### 状态
⏳ 待执行

---

## 用例 ID: TASK-002

### 用例描述
验证获取任务详情功能。

### 前置条件
- 后端服务已启动
- 已获取有效 Token
- 数据库中存在已知 ID 的任务

### 测试步骤

#### 步骤 1: 获取存在的任务详情
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -X GET https://plan.shujuyunxiang.com/api/v1/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

#### 步骤 2: 获取不存在的任务详情
```bash
curl -X GET https://plan.shujuyunxiang.com/api/v1/tasks/99999 \
  -H "Authorization: Bearer $TOKEN"
```

### 预期结果

| 步骤 | 预期状态码 | 预期响应内容 |
|------|------------|--------------|
| 步骤 1 | 200 | 任务详情 JSON 对象 |
| 步骤 2 | 404 | `{"code":404,"message":"任务不存在"}` |

### 实际结果
待执行...

### 状态
⏳ 待执行

---

## 用例 ID: TASK-003

### 用例描述
验证创建任务功能。

### 前置条件
- 后端服务已启动
- 已获取有效 Token

### 测试步骤

#### 步骤 1: 创建有效任务
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -X POST https://plan.shujuyunxiang.com/api/v1/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "测试任务",
    "description": "这是一个测试任务",
    "status": "pending",
    "priority": "medium",
    "dueDate": "2026-03-20"
  }'
```

#### 步骤 2: 创建空标题任务（验证失败）
```bash
curl -X POST https://plan.shujuyunxiang.com/api/v1/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "",
    "description": "标题为空"
  }'
```

### 预期结果

| 步骤 | 预期状态码 | 预期响应内容 |
|------|------------|--------------|
| 步骤 1 | 201 或 200 | 创建成功，返回任务 ID |
| 步骤 2 | 400 | 验证错误，提示标题不能为空 |

### 实际结果
待执行...

### 状态
⏳ 待执行

---

## 用例 ID: TASK-004

### 用例描述
验证更新任务功能。

### 前置条件
- 后端服务已启动
- 已获取有效 Token
- 数据库中存在已知 ID 的任务

### 测试步骤
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -X PUT https://plan.shujuyunxiang.com/api/v1/tasks/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "更新后的任务标题",
    "status": "in_progress",
    "priority": "high"
  }'
```

### 预期结果
- 状态码：200
- 响应内容：更新后的任务详情

### 实际结果
待执行...

### 状态
⏳ 待执行

---

## 用例 ID: TASK-005

### 用例描述
验证删除任务功能。

### 前置条件
- 后端服务已启动
- 已获取有效 Token
- 数据库中存在可删除的测试任务

### 测试步骤

#### 步骤 1: 删除存在的任务
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -X DELETE https://plan.shujuyunxiang.com/api/v1/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

#### 步骤 2: 验证任务已删除
```bash
curl -X GET https://plan.shujuyunxiang.com/api/v1/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 预期结果

| 步骤 | 预期状态码 | 预期响应内容 |
|------|------------|--------------|
| 步骤 1 | 200 或 204 | 删除成功 |
| 步骤 2 | 404 | 任务不存在 |

### 实际结果
待执行...

### 状态
⏳ 待执行

---

## 用例 ID: TASK-006

### 用例描述
验证任务进度上报功能。

### 前置条件
- 后端服务已启动
- 已获取有效 Token
- 数据库中存在已知 ID 的任务

### 测试步骤
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -X POST https://plan.shujuyunxiang.com/api/v1/tasks/1/progress \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "progress": 50,
    "comment": "已完成一半"
  }'
```

### 预期结果
- 状态码：200
- 响应内容：进度更新成功

### 实际结果
待执行...

### 状态
⏳ 待执行

---

**编写人**: qa-tester  
**编写日期**: 2026-03-16  
**优先级**: P0
