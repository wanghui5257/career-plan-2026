# SaaS 迭代开发闭环流程

**项目**: career-plan-2026（职业顾问 SaaS 服务）  
**版本**: 1.0  
**制定日期**: 2026-03-18  
**制定者**: saas-architect

---

## 📊 完整迭代流程图

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           迭代开发闭环流程 (2 周 Sprint)                          │
└─────────────────────────────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  迭代规划    │ ◄─────────────────────────────────────────────────┐
    │  (Day 1-2)   │                                                   │
    └──────┬───────┘                                                   │
           │ 输出：Sprint Backlog                                      │
           ▼                                                           │
    ┌──────────────┐                                                   │
    │   开发阶段   │                                                   │
    │  (Day 3-8)   │                                                   │
    └──────┬───────┘                                                   │
           │ 输出：功能代码 + 单元测试                                  │
           ▼                                                           │
    ┌──────────────┐                                                   │
    │   测试闭环   │──── 不通过 ────────────────────────────────────────┤
    │  (Day 7-10)  │                                                   │
    └──────┬───────┘                                                   │
           │ 输出：测试报告 + 质量门禁通过                              │
           ▼                                                           │
    ┌──────────────┐                                                   │
    │   代码审查   │──── 不通过 ────────────────────────────────────────┤
    │  (Day 9-10)  │                                                   │
    └──────┬───────┘                                                   │
           │ 输出：Approved PR                                         │
           ▼                                                           │
    ┌──────────────┐                                                   │
    │   部署阶段   │                                                   │
    │ (Day 11-12)  │                                                   │
    └──────┬───────┘                                                   │
           │ 输出：生产环境部署                                        │
           ▼                                                           │
    ┌──────────────┐                                                   │
    │  发布验证    │──── 发现问题 ──────────────────────────────────────┤
    │  (Day 12)    │         (触发回滚)                                 │
    └──────┬───────┘                                                   │
           │ 输出：验证报告                                            │
           ▼                                                           │
    ┌──────────────┐                                                   │
    │  反馈闭环    │───────────────────────────────────────────────────┘
    │ (持续进行)   │     下一迭代输入
    └──────────────┘
```

---

## 1️⃣ 迭代规划阶段 (Day 1-2)

### 1.1 输入
- 产品需求文档 (PRD)
- 用户反馈和问题列表
- 上一迭代的回顾总结
- 技术债务清单

### 1.2 流程步骤

```
需求收集 → 需求分析 → 任务分解 → 工时估算 → Sprint 目标设定 → Sprint Backlog 确认
```

### 1.3 详细活动

| 活动 | 负责人 | 产出物 | 时间 |
|------|--------|--------|------|
| 需求收集 | 产品经理 | 需求清单 | Day 1 AM |
| 需求分析和优先级排序 | 产品经理 + 架构师 | 优先级需求列表 | Day 1 PM |
| 任务分解 (拆分为可执行任务) | 开发团队 | 任务清单 (Jira/GitHub Issues) | Day 2 AM |
| 工时估算 (Planning Poker) | 开发团队 | 估算工时 | Day 2 AM |
| Sprint 目标设定 | 全员 | Sprint Goal | Day 2 PM |
| Sprint Backlog 确认 | 全员 | 确认的 Sprint Backlog | Day 2 PM |

### 1.4 输出
- ✅ Sprint Goal（迭代目标）
- ✅ Sprint Backlog（迭代任务清单）
- ✅ 任务优先级排序
- ✅ 工时估算

### 1.5 质量门禁
- [ ] 所有需求都有明确的验收标准 (Acceptance Criteria)
- [ ] 任务粒度不超过 2 天工作量
- [ ] 团队对 Sprint 目标达成共识
- [ ] 风险已识别并记录

---

## 2️⃣ 开发阶段 (Day 3-8)

### 2.1 分支策略：GitHub Flow + Release Branch

```
main (生产)
  │
  ├─── release/v1.x.x (发布分支，测试/预发)
  │       │
  │       └─── 稳定后合并到 main，打 tag
  │
  └─── feature/xxx (功能分支，从 main 切出)
          │
          └─── 开发完成后提 PR → release 分支
```

**分支命名规范**:
- 功能分支：`feature/<jira-id>-<short-description>` (例：`feature/CAREER-101-user-auth`)
- 修复分支：`fix/<jira-id>-<short-description>` (例：`fix/CAREER-102-login-bug`)
- 发布分支：`release/v<major>.<minor>.<patch>` (例：`release/v1.2.0`)
- 热修复分支：`hotfix/<jira-id>-<short-description>` (例：`hotfix/CAREER-103-critical-bug`)

### 2.2 代码规范

**Python 项目规范**:
```yaml
代码风格: PEP 8
类型检查: mypy (strict 模式)
格式化: black + isort
Lint: flake8 + pylint
文档: Google Style Docstrings
```

**提交规范 (Conventional Commits)**:
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type 类型**:
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档变更
- `style`: 代码格式 (不影响功能)
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具/配置

**示例**:
```bash
feat(auth): 添加用户登录功能

- 实现 JWT token 生成和验证
- 添加登录 API 端点
- 添加密码加密

Closes: CAREER-101
```

### 2.3 输入
- Sprint Backlog
- 设计文档
- API 规范

### 2.4 输出
- ✅ 功能代码
- ✅ 单元测试代码
- ✅ 代码文档
- ✅ 提交记录

### 2.5 质量门禁
- [ ] 代码通过 Lint 检查
- [ ] 代码通过类型检查
- [ ] 提交信息符合规范
- [ ] 每个功能都有对应的单元测试

---

## 3️⃣ 测试闭环 (Day 7-10) ⭐ 关键环节

### 3.1 测试金字塔

```
           ╱╲
          ╱  ╲         E2E 测试 (10%)
         ╱────╲        完整用户流程
        ╱      ╲
       ╱────────╲     集成测试 (20%)
      ╱          ╲    API/服务间调用
     ╱────────────╲
    ╱              ╲   单元测试 (70%)
   ╱────────────────╲  函数/类级别
```

### 3.2 单元测试

**要求**:
- **覆盖率**: 新增代码覆盖率 ≥ 90%，整体覆盖率 ≥ 80%
- **框架**: pytest (Python)
- **Mock**: unittest.mock / pytest-mock
- **执行时机**: 每次 commit 触发 (pre-commit hook)，CI 流水线必跑

**测试文件结构**:
```
tests/
├── unit/
│   ├── test_auth.py
│   ├── test_user_service.py
│   └── test_plan_generator.py
├── integration/
│   ├── test_api_auth.py
│   └── test_database.py
└── e2e/
    ├── test_user_journey.py
    └── test_plan_workflow.py
```

**示例**:
```python
# tests/unit/test_auth.py
import pytest
from src.auth import TokenService

class TestTokenService:
    def test_generate_token_returns_valid_jwt(self):
        service = TokenService(secret_key="test")
        token = service.generate_token(user_id=123)
        assert service.verify_token(token) == {"user_id": 123}
    
    def test_expired_token_raises_error(self):
        service = TokenService(secret_key="test")
        token = service.generate_token(user_id=123, expires_in=-1)
        with pytest.raises(TokenExpiredError):
            service.verify_token(token)
```

### 3.3 集成测试

**要求**:
- 覆盖所有 API 端点
- 覆盖服务间调用
- 使用测试数据库 (隔离)
- **执行时机**: PR 创建/更新时触发

**测试内容**:
- API 请求/响应验证
- 数据库操作验证
- 第三方服务集成 (Mock)
- 消息队列集成

**示例**:
```python
# tests/integration/test_api_auth.py
import pytest
from fastapi.testclient import TestClient
from src.main import app

client = TestClient(app)

class TestAuthAPI:
    def test_login_success(self, test_db):
        response = client.post("/api/auth/login", json={
            "email": "test@example.com",
            "password": "password123"
        })
        assert response.status_code == 200
        assert "access_token" in response.json()
    
    def test_login_invalid_credentials(self, test_db):
        response = client.post("/api/auth/login", json={
            "email": "test@example.com",
            "password": "wrongpassword"
        })
        assert response.status_code == 401
```

### 3.4 E2E 测试

**要求**:
- 覆盖核心用户流程
- 真实环境或 Staging 环境执行
- **执行时机**: 部署到 Staging 后自动触发

**核心流程覆盖**:
1. 用户注册 → 登录 → 创建职业规划 → 查看报告
2. 用户登录 → 上传简历 → AI 分析 → 获取建议
3. 用户登录 → 订阅服务 → 支付 → 解锁高级功能

**示例** (使用 Playwright):
```python
# tests/e2e/test_user_journey.py
import pytest
from playwright.sync_api import sync_playwright

def test_complete_user_journey():
    with sync_playwright() as p:
        browser = p.chromium.launch()
        page = browser.new_page()
        
        # 注册
        page.goto("https://staging.career-plan.com")
        page.click("text=注册")
        page.fill("input[name=email]", "e2etest@example.com")
        page.fill("input[name=password]", "Test123!")
        page.click("button[type=submit]")
        
        # 创建规划
        page.click("text=创建职业规划")
        page.fill("textarea[name=goal]", "成为软件架构师")
        page.click("text=生成规划")
        
        # 验证结果
        assert page.is_visible("text=你的职业规划已生成")
        
        browser.close()
```

### 3.5 测试通过标准 (Quality Gates)

| 测试类型 | 通过标准 | 不通过处理 |
|----------|----------|------------|
| 单元测试 | 覆盖率 ≥ 90% (新增代码), 100% 通过 | 阻断 CI，禁止合并 |
| 集成测试 | 100% 通过 | 阻断 PR 合并 |
| E2E 测试 | 核心流程 100% 通过 | 阻断发布，触发回滚 |
| 性能测试 | P95 响应时间 < 500ms | 优化后重新测试 |
| 安全扫描 | 无高危漏洞 | 修复后重新扫描 |

### 3.6 输入
- 功能代码
- 测试环境

### 3.7 输出
- ✅ 测试报告
- ✅ 覆盖率报告
- ✅ 质量门禁检查结果

---

## 4️⃣ 代码审查 (Day 9-10)

### 4.1 Review 流程

```
开发者创建 PR → 自动化检查 → 分配 Reviewer → 代码审查 → 反馈修改 → 批准 → 合并
```

### 4.2 自动化检查 (PR 触发)

| 检查项 | 工具 | 通过标准 |
|--------|------|----------|
| 代码风格 | black, isort | 无格式问题 |
| Lint 检查 | flake8, pylint | 无 Error，Warning < 10 |
| 类型检查 | mypy | 无类型错误 |
| 单元测试 | pytest | 100% 通过，覆盖率达标 |
| 集成测试 | pytest | 100% 通过 |
| 安全扫描 | bandit, safety | 无高危漏洞 |
| 依赖检查 | dependabot | 无已知漏洞 |

### 4.3 人工审查清单

**Reviewer 检查项**:
- [ ] 代码逻辑正确，符合需求
- [ ] 代码可读性好，命名清晰
- [ ] 有适当的注释和文档
- [ ] 错误处理完善
- [ ] 没有硬编码的配置/密钥
- [ ] 数据库查询有索引优化
- [ ] API 设计符合 REST 规范
- [ ] 日志记录适当

### 4.4 批准标准

**PR 合并条件**:
- [ ] 至少 1 名资深开发者批准
- [ ] 所有自动化检查通过
- [ ] 所有评论已解决
- [ ] 分支已 rebase 到最新 main

### 4.5 输入
- PR (Pull Request)
- 自动化检查报告

### 4.6 输出
- ✅ Approved PR
- ✅ 审查意见记录
- ✅ 合并后的代码

---

## 5️⃣ 部署阶段 (Day 11-12)

### 5.1 CI/CD 流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CI/CD Pipeline                                   │
└─────────────────────────────────────────────────────────────────────────┘

  Code Push → Lint/Test → Build → Push Image → Deploy Dev → Test
                                                          │
                                                          ▼
  Production ← Rollback? ← Monitor ← Deploy Prod ← Deploy Staging ← E2E
```

### 5.2 环境管理

| 环境 | 用途 | 部署触发 | 数据 |
|------|------|----------|------|
| **Dev** | 开发测试 | 每次 PR 合并到 main | Mock/测试数据 |
| **Staging** | 预发验证 | 创建 release 分支 | 脱敏生产数据 |
| **Production** | 生产环境 | release 分支合并到 main | 真实数据 |

### 5.3 部署策略

**蓝绿部署** (推荐):
```
                    ┌──────────────┐
   流量 ────────────►│   Load       │
                    │   Balancer   │
                    └──────┬───────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
           ▼               ▼               ▼
      ┌─────────┐    ┌─────────┐    ┌─────────┐
      │  Blue   │    │  Green  │    │  Idle   │
      │ (v1.1)  │    │ (v1.2)  │    │         │
      │  Live   │    │  Ready  │    │         │
      └─────────┘    └─────────┘    └─────────┘
           │               │
           └───────┬───────┘
                   │
          切换流量 (瞬间完成)
```

### 5.4 回滚策略

**自动回滚条件**:
- E2E 测试失败
- 健康检查失败 (连续 3 次)
- 错误率 > 5% (5 分钟内)
- P95 响应时间 > 2s (5 分钟内)

**回滚流程**:
```
检测到问题 → 自动切换流量到旧版本 → 发送告警 → 记录事件 → 问题分析 → 修复
```

**回滚命令示例**:
```bash
# Kubernetes 回滚
kubectl rollout undo deployment/career-plan-api

# Docker Swarm 回滚
docker service update --rollback career-plan-api
```

### 5.5 输入
- Approved 的代码
- 构建产物 (Docker Image)

### 5.6 输出
- ✅ 部署到各环境的系统
- ✅ 部署日志
- ✅ 版本号

---

## 6️⃣ 发布验证 (Day 12)

### 6.1 健康检查

**检查项** (部署后 5 分钟内完成):

| 检查项 | 方法 | 预期结果 |
|--------|------|----------|
| 服务存活 | `GET /health` | 200 OK |
| 数据库连接 | `GET /health/db` | 200 OK, 响应 < 100ms |
| 缓存连接 | `GET /health/redis` | 200 OK |
| 外部服务 | `GET /health/external` | 200 OK |
| 磁盘空间 | 监控指标 | 使用率 < 80% |
| 内存使用 | 监控指标 | 使用率 < 80% |

### 6.2 核心功能验证

**Smoke Test 清单**:

- [ ] 用户可以登录/登出
- [ ] 用户可以创建职业规划
- [ ] AI 分析功能正常
- [ ] 报告生成功能正常
- [ ] 支付流程正常 (如本次发布涉及)
- [ ] 关键 API 响应正常

### 6.3 监控告警

**监控指标**:
- 请求量 (RPS)
- 错误率 (%)
- 响应时间 (P50, P95, P99)
- 系统资源 (CPU, 内存，磁盘)
- 业务指标 (日活，转化率)

**告警规则**:

| 指标 | 阈值 | 级别 | 通知方式 |
|------|------|------|----------|
| 错误率 | > 5% (5min) | P0 | 电话 + 短信 + 钉钉 |
| 响应时间 P95 | > 2s (5min) | P1 | 短信 + 钉钉 |
| 服务宕机 | 1min | P0 | 电话 + 短信 + 钉钉 |
| CPU 使用率 | > 90% (10min) | P2 | 钉钉 |
| 内存使用率 | > 90% (10min) | P2 | 钉钉 |

### 6.4 输入
- 部署完成的生产环境
- 监控面板

### 6.5 输出
- ✅ 验证报告
- ✅ 监控基线
- ✅ 发布确认

---

## 7️⃣ 反馈闭环 (持续进行)

### 7.1 问题收集渠道

```
                    ┌─────────────────┐
                    │   问题收集中心   │
                    └────────┬────────┘
                             │
    ┌────────────────────────┼────────────────────────┐
    │                        │                        │
    ▼                        ▼                        ▼
┌─────────┐          ┌─────────────┐          ┌─────────────┐
│ 用户反馈 │          │  监控告警   │          │  系统日志   │
│ - 工单  │          │ - 错误率    │          │ - 异常堆栈  │
│ - 邮件  │          │ - 性能问题  │          │ - 慢查询    │
│ - 客服  │          │ - 资源告警  │          │ - 访问日志  │
└─────────┘          └─────────────┘          └─────────────┘
```

### 7.2 问题分类和优先级

| 优先级 | 响应时间 | 解决时间 | 示例 |
|--------|----------|----------|------|
| **P0** | 15 分钟 | 4 小时 | 服务宕机，数据丢失 |
| **P1** | 1 小时 | 24 小时 | 核心功能不可用 |
| **P2** | 4 小时 | 3 天 | 非核心功能问题 |
| **P3** | 1 天 | 下个迭代 | 体验优化，小 Bug |

### 7.3 迭代改进流程

**Sprint 回顾会议** (每个迭代结束):

```
收集反馈 → 分析问题 → 识别改进点 → 制定行动计划 → 纳入下迭代
```

**回顾会议议程**:
1. 本迭代目标达成情况
2. 做得好的地方 (Keep)
3. 需要改进的地方 (Improve)
4. 需要停止的做法 (Stop)
5. 下迭代行动计划 (Action Items)

### 7.4 输入
- 用户反馈
- 监控数据
- 问题工单

### 7.5 输出
- ✅ 问题清单
- ✅ 改进计划
- ✅ 下一迭代的需求输入

---

## 📋 角色职责

| 角色 | 职责 | 参与环节 |
|------|------|----------|
| **产品经理** | 需求定义、优先级排序、验收 | 规划、验证、反馈 |
| **架构师** | 技术选型、架构设计、代码审查 | 规划、开发、审查 |
| **开发工程师** | 功能开发、单元测试、Bug 修复 | 开发、测试、审查 |
| **测试工程师** | 测试计划、集成/E2E 测试、质量把关 | 测试、验证 |
| **DevOps** | CI/CD、部署、监控、告警 | 部署、验证 |
| **Tech Lead** | 代码审查、技术决策、团队协调 | 审查、部署 |

---

## 🤖 自动化建议

### 高度自动化 (推荐)

| 环节 | 自动化内容 | 工具建议 |
|------|------------|----------|
| 代码检查 | Lint、格式化、类型检查 | pre-commit hooks, GitHub Actions |
| 单元测试 | 每次 commit 自动运行 | pytest + GitHub Actions |
| 集成测试 | PR 触发自动运行 | pytest + GitHub Actions |
| 构建部署 | 自动构建、推送镜像、部署 | GitHub Actions + ArgoCD |
| E2E 测试 | 部署 Staging 后自动触发 | Playwright + GitHub Actions |
| 健康检查 | 部署后自动执行 | 自定义脚本 + Kubernetes probes |
| 监控告警 | 7x24 自动监控 | Prometheus + Grafana + AlertManager |
| 回滚 | 满足条件自动回滚 | Argo Rollouts |

### 半自动化 (人机协作)

| 环节 | 自动化内容 | 人工介入点 |
|------|------------|------------|
| 代码审查 | 自动化检查报告 | 人工审查逻辑、设计 |
| 部署审批 | 自动部署到 Dev/Staging | 生产部署需人工确认 |
| 问题分类 | AI 初步分类 | 人工确认优先级 |

### 人工主导

| 环节 | 原因 |
|------|------|
| 需求优先级 | 需要业务判断 |
| Sprint 目标 | 需要团队共识 |
| 架构决策 | 需要经验判断 |
| 紧急问题处理 | 需要灵活决策 |

---

## 📎 附录

### A. 工具链推荐

```yaml
版本控制: GitHub
项目管理: Jira / GitHub Projects
CI/CD: GitHub Actions / GitLab CI
容器化: Docker
编排: Kubernetes
监控: Prometheus + Grafana
日志: ELK Stack
测试: pytest + Playwright
代码质量: SonarQube
```

### B. 关键指标 (KPI)

| 指标 | 目标值 | 测量方式 |
|------|--------|----------|
| 迭代交付率 | ≥ 90% | 完成故事点/计划故事点 |
| 代码覆盖率 | ≥ 80% | pytest-cov |
| Bug 逃逸率 | < 5% | 生产 Bug 数/总 Bug 数 |
| 部署频率 | ≥ 2 周/次 | 部署记录 |
| 变更失败率 | < 10% | 回滚次数/部署次数 |
| 平均恢复时间 | < 1 小时 | 故障记录 |

### C. 文档模板

- [Sprint 计划模板](./templates/sprint_plan.md)
- [PR 审查清单](./templates/pr_checklist.md)
- [发布验证清单](./templates/release_checklist.md)
- [回顾会议模板](./templates/retrospective.md)

---

**文档维护**: 本流程文档应每季度回顾更新，确保持续改进。  
**最后更新**: 2026-03-18  
**下次回顾**: 2026-06-18
