# 测试规范 (Testing Standards)

## 目的
确保测试质量、覆盖率和可重复性。

## 适用范围
- API 测试
- 前端测试
- 集成测试

---

## API 测试规范

### 1. 测试脚本结构
```bash
#!/bin/bash

# 基础配置
BASE_URL="https://staging.plan.shujuyunxiang.com/back-server"
TEST_REPORT="/root/tasks/task-3.1/api-test-results.md"

# 测试用例
run_test() {
    local name="$1"
    local method="$2"
    local endpoint="$3"
    local data="$4"
    local expected_code="$5"
    local auth_token="$6"  # 可选，无授权测试不传
    
    # 执行测试
    # ...
    
    # 验证结果（检查 JSON code 字段）
    # ...
}
```

### 2. HTTP 方法规范
| API | 方法 | 说明 |
|------|------|------|
| 登录 | POST | 创建会话 |
| 用户资料获取 | GET | 查询资源 |
| 用户资料更新 | PUT | 更新资源 |
| 密码修改 | PUT | 更新资源 |
| 任务确认 | POST | 创建操作 |

```bash
# ✅ 正确：使用正确的 HTTP 方法
curl -X PUT $BASE_URL/api/v1/user/password \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"...","newPassword":"..."}'

# ❌ 错误：使用 POST 而非 PUT
curl -X POST $BASE_URL/api/v1/user/password \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"...","newPassword":"..."}'
```

### 3. 响应验证规范
```bash
# ✅ 正确：检查 JSON code 字段
response=$(curl -s -w "\n%{http_code}" ...)
http_code=$(echo "$response" | tail -1)
body=$(echo "$response" | head -n -1)
code=$(echo "$body" | jq -r '.code')

if [ "$code" -eq "$expected_code" ]; then
    echo "✅ PASS: $name"
else
    echo "❌ FAIL: $name (期望:$expected_code, 实际:$code)"
fi

# ❌ 错误：检查 HTTP 状态码（后端统一返回 200）
if [ "$http_code" -eq "$expected_code" ]; then
    # 后端业务状态在 JSON code 字段，不在 HTTP 状态
fi
```

### 4. 无授权测试规范
```bash
# ✅ 正确：真正无 Token
run_test "UPD-005" "未授权访问" "PUT" "/api/v1/user/profile" \
  '{"name":"test"}' "401" ""  # ← 空字符串，不使用默认 Token

# ❌ 错误：测试脚本 Bug（已修复）
# local auth_token="${7:-$TOKEN}"  # ← 空字符串回退到默认 Token
# 导致无授权测试实际使用了 Token，返回 200（假阴性）
```

---

## 测试清理机制

### 1. 密码恢复
```bash
# ✅ 正确：每个修改密码用例后立即恢复
test_PWD_001() {
    # 测试修改密码
    run_test "PWD-001" "修改密码" "PUT" "/api/v1/user/password" \
      '{"oldPassword":"123456","newPassword":"Test123456"}' "200" "$TOKEN"
    
    # 立即恢复原始密码
    restore_password "admin" "123456"
}

restore_password() {
    local username="$1"
    local password="$2"
    mysql -u root -p -e "USE career_plan; UPDATE users SET password='...' WHERE username='$username';"
}
```

### 2. 使用 trap 确保清理
```bash
# ✅ 正确：使用 trap 确保清理执行
cleanup() {
    echo "恢复所有测试账号密码..."
    restore_password "admin" "123456"
    restore_password "testuser" "test123"
    restore_password "advisor1" "Advisor1234!"
}

trap cleanup EXIT
```

### 3. 测试前备份
```bash
# ✅ 正确：测试前备份原始数据
setup() {
    echo "备份原始密码..."
    mysql -u root -p -e "USE career_plan; SELECT username, password INTO OUTFILE '/tmp/password_backup.csv';"
}
```

---

## 测试数据管理

### 1. 测试账号
| 账号 | 密码 | 角色 | 用途 |
|------|------|------|------|
| admin | 123456 | ADMIN | 管理员功能测试 |
| testuser | test123 | PLAN_CREATOR | 普通用户功能测试 |
| advisor1 | Advisor1234! | SUPERVISOR | 顾问功能测试 |

### 2. 测试任务数据
```sql
-- ✅ 正确：创建测试任务
INSERT INTO tasks (id, title, assigned_user_id, status)
VALUES (1001, '测试任务', 2, 'PENDING');  -- assigned to testuser (id=2)

-- ❌ 错误：任务未分配给测试账号
INSERT INTO tasks (id, title, assigned_user_id, status)
VALUES (1001, '测试任务', 999, 'PENDING');  -- user 999 不存在
```

### 3. 测试数据隔离
- 测试数据使用独立 ID 范围（1000+）
- 测试完成后清理测试数据
- 禁止使用生产数据测试

---

## 测试报告规范

### 1. 报告格式
```markdown
# API 测试报告

## 测试结果汇总
| API | 用例数 | 通过 | 失败 | 通过率 |
|------|--------|------|------|--------|
| 修改密码 API | 10 | 8 | 2 | 80.0% |
| 用户资料 API | 5 | 2 | 3 | 40.0% |
| **总计** | **26** | **19** | **7** | **73.1%** |

## 失败用例分析
| 用例 | 期望 | 实际 | 根因 |
|------|------|------|------|
| PWD-004 | 400 | 200 | 密码长度上限未验证 |
| ... | ... | ... | ... |
```

### 2. 根因分类
| 类别 | 说明 | 处理 |
|------|------|------|
| P0 安全漏洞 | 未授权访问返回 200 | 立即修复 |
| P1 功能缺失 | 密码长度上限未验证 | 优先修复 |
| 测试脚本问题 | 密码未恢复、逻辑错误 | 改进脚本 |
| 测试数据问题 | 任务未分配、期望值错误 | 准备数据 |

### 3. 有效通过率计算
```
有效通过率 = 通过用例数 / (总用例数 - 测试数据/脚本问题数)

示例：
- 总用例：26
- 通过：19
- 测试数据/脚本问题：5
- 有效通过率 = 19 / (26 - 5) = 90.5%
```

---

## 前端测试规范

### 1. 组件测试
```javascript
// ✅ 正确：测试组件渲染
describe('ChangePasswordForm', () => {
    it('should render form fields', () => {
        render(<ChangePasswordForm />);
        expect(screen.getByLabelText('旧密码')).toBeInTheDocument();
        expect(screen.getByLabelText('新密码')).toBeInTheDocument();
    });
});
```

### 2. 表单验证测试
```javascript
// ✅ 正确：测试密码强度验证
it('should show error for weak password', () => {
    render(<ChangePasswordForm />);
    fireEvent.change(screen.getByLabelText('新密码'), {
        target: { value: '123' }
    });
    expect(screen.getByText('密码长度至少 8 位')).toBeInTheDocument();
});
```

### 3. API 调用测试
```javascript
// ✅ 正确：模拟 API 调用
jest.mock('../services/userService');
userService.changePassword.mockResolvedValue({ code: 200 });

it('should call changePassword API', async () => {
    render(<ChangePasswordForm />);
    // ... 填写表单并提交
    expect(userService.changePassword).toHaveBeenCalledWith({
        oldPassword: '...',
        newPassword: '...'
    });
});
```

---

## 测试覆盖率要求

| 类型 | 要求 | 工具 |
|------|------|------|
| 后端单元测试 | ≥ 80% | JaCoCo |
| API 测试 | ≥ 90% | 自定义脚本 |
| 前端组件测试 | ≥ 70% | Jest + RTL |
| 前端联调测试 | ≥ 90% | 手动/自动化 |

---

## 常见问题

### Q1: 测试通过率不达标
**原因**：测试数据问题或功能缺陷
**解决**：
1. 分析失败用例根因
2. 区分功能缺陷和测试数据问题
3. 修复功能缺陷或补充测试数据

### Q2: 测试脚本污染数据
**原因**：清理机制不完善
**解决**：
1. 每个修改用例后立即恢复
2. 使用 `trap` 确保清理执行
3. 测试前备份原始数据

### Q3: 无授权测试返回 200
**原因**：测试脚本使用了默认 Token
**解决**：
1. 检查 `auth_token` 参数传递
2. 确保无回退逻辑：`${7:-$TOKEN}` → `$7`
3. 手动验证：`curl` 不带 `-H "Authorization: ..."`

---

## 修订历史
| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| 1.0 | 2026-03-19 | career-plan-team | Phase 2 初始版本 |
