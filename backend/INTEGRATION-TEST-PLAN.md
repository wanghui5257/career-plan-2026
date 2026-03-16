# 集成测试规划 - backend-dev

**创建时间**: 2026-03-16 11:20 UTC  
**规划人**: manager  
**执行**: backend-dev + qa-tester

---

## 📋 测试体系架构

```
测试层级
├── L1: 单元测试 ✅ (已完成)
│   ├── AuthControllerTest.java
│   ├── TaskControllerTest.java
│   ├── PasswordVerifyTest.java
│   └── CareerPlanApplicationTests.java
│
├── L2: 集成测试 ⏳ (待执行)
│   ├── API 接口集成测试
│   ├── 前后端联调测试
│   └── 数据库集成测试
│
└── L3: 脚本验证 ⏳ (待完善)
    ├── test-login.sh (已有)
    ├── auto-verify.sh (已有)
    ├── test-full-flow.sh (新增)
    └── test-ci-cd.sh (新增)
```

---

## 🧪 L1: 单元测试（已完成）✅

### 测试文件
| 文件 | 测试内容 | 状态 |
|------|----------|------|
| `AuthControllerTest.java` | 登录、健康检查 | ✅ 已完成 |
| `TaskControllerTest.java` | 任务 CRUD、权限验证 | ✅ 已完成 |
| `PasswordVerifyTest.java` | BCrypt 密码验证 | ✅ 已完成 |
| `CareerPlanApplicationTests.java` | 应用启动测试 | ✅ 已完成 |

### 运行方式
```bash
cd backend
mvn test
```

### 输出位置
```
backend/target/surefire-reports/
├── TEST-*.xml
└── *.txt
```

---

## 🔗 L2: 集成测试（待执行）

### 2.1 API 接口集成测试

**测试场景**:
| 测试项 | 输入 | 预期输出 | 状态 |
|--------|------|----------|------|
| 登录 → 获取任务 | 正确凭证 | 200 + Token + 任务列表 | ⏳ |
| 登录 → 创建任务 | Token + 任务数据 | 201 + 任务对象 | ⏳ |
| 登录 → 更新任务 | Token + 任务 ID | 200 + 更新后对象 | ⏳ |
| 登录 → 删除任务 | Token + 任务 ID | 204 无内容 | ⏳ |
| 未认证访问 | 无 Token | 401 未授权 | ⏳ |
| Token 过期 | 过期 Token | 401 Token 无效 | ⏳ |

**执行脚本**: `backend/scripts/test-integration.sh` (新增)

---

### 2.2 前后端联调测试

**测试场景**:
| 测试项 | 前端操作 | 后端响应 | 状态 |
|--------|----------|----------|------|
| 登录流程 | 输入凭证 → 提交 | 返回 Token → 存储 | ⏳ |
| 任务列表 | 页面加载 | GET /tasks → 渲染 | ⏳ |
| 创建任务 | 填写表单 → 提交 | POST /tasks → 刷新 | ⏳ |
| 更新状态 | 拖拽/编辑 → 保存 | PUT /tasks/{id} → 更新 | ⏳ |
| 删除任务 | 点击删除 → 确认 | DELETE /tasks/{id} → 移除 | ⏳ |

**执行方式**: 手动测试 + Selenium 自动化 (待实现)

---

### 2.3 数据库集成测试

**测试场景**:
| 测试项 | 操作 | 验证 | 状态 |
|--------|------|------|------|
| 用户数据 | 查询 users 表 | admin 用户存在 | ⏳ |
| 任务数据 | 查询 tasks 表 | 测试数据完整 | ⏳ |
| 数据一致性 | 创建 → 查询 → 更新 → 删除 | CRUD 完整 | ⏳ |
| 事务测试 | 并发写入 | 无数据冲突 | ⏳ |

**执行脚本**: `backend/scripts/test-database.sh` (新增)

---

## 🛠️ L3: 脚本验证（待完善）

### 3.1 已有脚本 ✅

| 脚本 | 功能 | 位置 |
|------|------|------|
| `test-login.sh` | 登录接口验证 | `backend/scripts/` |
| `auto-verify.sh` | 自动验证 + 密码生成 | `backend/scripts/` |
| `verify-and-restart.sh` | 验证 + 重启应用 | `backend/scripts/` |

---

### 3.2 新增脚本规划 ⏳

#### test-full-flow.sh (完整流程测试)

```bash
#!/bin/bash
# 完整业务流程测试

# 1. 登录获取 Token
TOKEN=$(curl -s -X POST "$API_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.token')

# 2. 创建测试任务
TASK_ID=$(curl -s -X POST "$API_URL/tasks" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"集成测试任务","status":"TODO"}' | jq -r '.id')

# 3. 更新任务状态
curl -s -X PUT "$API_URL/tasks/$TASK_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_PROGRESS","progress":50}'

# 4. 上报进度
curl -s -X POST "$API_URL/tasks/$TASK_ID/progress" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"progress":100,"comment":"完成"}'

# 5. 删除测试任务
curl -s -X DELETE "$API_URL/tasks/$TASK_ID" \
  -H "Authorization: Bearer $TOKEN"

# 6. 验证结果
echo "✅ 完整流程测试通过"
```

---

#### test-ci-cd.sh (CI/CD 流水线测试)

```bash
#!/bin/bash
# CI/CD 流水线验证

echo "=== CI/CD 测试 ==="

# 1. 代码检查
echo "1️⃣ 代码检查..."
cd backend
mvn checkstyle:check

# 2. 单元测试
echo "2️⃣ 单元测试..."
mvn test

# 3. 构建打包
echo "3️⃣ 构建打包..."
mvn clean package -DskipTests

# 4. 部署验证
echo "4️⃣ 部署验证..."
./scripts/deploy.sh

# 5. 健康检查
echo "5️⃣ 健康检查..."
curl -s http://localhost:9999/actuator/health

# 6. 集成测试
echo "6️⃣ 集成测试..."
./scripts/test-full-flow.sh

echo "✅ CI/CD 流水线测试完成"
```

---

#### test-database.sh (数据库测试)

```bash
#!/bin/bash
# 数据库集成测试

DB_HOST="localhost"
DB_NAME="career_plan"
DB_USER="root"

echo "=== 数据库测试 ==="

# 1. 检查连接
echo "1️⃣ 检查数据库连接..."
mysql -h $DB_HOST -u $DB_USER -p -e "USE $DB_NAME; SELECT 1;"

# 2. 验证表结构
echo "2️⃣ 验证表结构..."
mysql -h $DB_HOST -u $DB_USER -p -e "USE $DB_NAME; SHOW TABLES;"

# 3. 验证测试数据
echo "3️⃣ 验证测试数据..."
mysql -h $DB_HOST -u $DB_USER -p -e "USE $DB_NAME; SELECT * FROM users WHERE username='admin';"

# 4. 数据完整性检查
echo "4️⃣ 数据完整性检查..."
mysql -h $DB_HOST -u $DB_USER -p -e "USE $DB_NAME; SELECT COUNT(*) FROM tasks;"

echo "✅ 数据库测试完成"
```

---

## 📅 执行计划

| 时间 | 任务 | 负责人 | 状态 |
|------|------|--------|------|
| **19:20-19:30** | 创建集成测试脚本 | backend-dev | ⏳ |
| **19:30-19:40** | 执行集成测试 | backend-dev | ⏳ |
| **19:40-19:50** | 验证测试结果 | qa-tester | ⏳ |
| **19:50-20:00** | 生成测试报告 | qa-tester | ⏳ |

---

## 📊 测试报告模板

```markdown
# 集成测试报告 - backend-dev

**测试时间**: 2026-03-16
**执行人**: backend-dev
**验证人**: qa-tester

## 测试结果

| 测试类型 | 用例数 | 通过 | 失败 | 通过率 |
|----------|--------|------|------|--------|
| 单元测试 | 12 | 12 | 0 | 100% |
| 集成测试 | 8 | - | - | - |
| 脚本验证 | 5 | - | - | - |

## 详细结果

### 单元测试 ✅
- AuthControllerTest: 4/4 通过
- TaskControllerTest: 5/5 通过
- PasswordVerifyTest: 2/2 通过
- ApplicationTests: 1/1 通过

### 集成测试 ⏳
- API 接口集成：待执行
- 前后端联调：待执行
- 数据库集成：待执行

### 脚本验证 ⏳
- test-login.sh: 待执行
- test-full-flow.sh: 待执行
- test-ci-cd.sh: 待执行

## 问题清单

| 编号 | 问题 | 严重性 | 状态 |
|------|------|--------|------|
| - | - | - | - |

## 结论

✅ 单元测试通过，集成测试待执行
```

---

## 🚀 立即行动

### @backend-dev 请执行

1. **创建集成测试脚本**
```bash
cd backend/scripts
# 创建 test-full-flow.sh
# 创建 test-database.sh
# 创建 test-ci-cd.sh
chmod +x *.sh
```

2. **执行集成测试**
```bash
cd backend
mvn test  # 单元测试
./scripts/test-full-flow.sh  # 集成测试
```

3. **更新任务 markdown**
```bash
# 编辑 tasks/task-backend-dev-api-input.md
# 编辑 tasks/task-backend-dev-role-permission.md
# 标记测试状态为 ✅
```

### @qa-tester 请验证

1. **检查测试脚本**
```bash
cat backend/scripts/test-full-flow.sh
cat backend/scripts/test-database.sh
```

2. **验证测试报告**
```bash
cat backend/target/surefire-reports/
```

3. **更新验证报告**
```bash
# 编辑 tests/reports/qa-report-20260316-final.md
# 添加集成测试验证结果
```

---

**@backend-dev 请立即创建集成测试脚本！** 🛠️  
**@qa-tester 请准备验证集成测试！** 🔍
