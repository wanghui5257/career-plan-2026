# 迭代开发流程规范

**版本**: 1.0  
**生效日期**: 2026-03-19  
**适用范围**: career-plan-2026 项目所有迭代开发

---

## 📋 目录

1. [开发流程概述](#1-开发流程概述)
2. [分支管理](#2-分支管理)
3. [代码审查](#3-代码审查)
4. [测试要求](#4-测试要求)
5. [部署流程](#5-部署流程)
6. [经验教训](#6-经验教训)

---

## 1. 开发流程概述

### 1.1 标准迭代流程

```
┌─────────────────────────────────────────────────────────────┐
│                    迭代开发流程                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 开发者完成代码 + 自测验证                                │
│     ↓                                                       │
│  2. 创建功能分支 (feature/xxx)                              │
│     ↓                                                       │
│  3. 推送到 GitHub                                           │
│     ↓                                                       │
│  4. 创建 PR (feature/xxx → main)                            │
│     ↓                                                       │
│  5. saas-architect 审查 PR（代码 + 测试报告）                 │
│     ↓                                                       │
│  6. 审查通过 → 添加审查评论并批准                            │
│     ↓                                                       │
│  7. 管理员合并 PR                                           │
│     ↓                                                       │
│  8. 生产部署 + 验证                                         │
│     ↓                                                       │
│  9. 迭代完成确认                                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 角色职责

| 角色 | 职责 |
|------|------|
| **开发者** | 代码开发、自测验证、创建 PR |
| **saas-architect** | 代码审查、规范合规检查 |
| **管理员** | PR 合并审批、生产部署决策 |
| **qa-tester** | API 测试、测试报告 |
| **alice** | 前端测试、前端验证 |
| **career-worker** | 后端部署、生产验证 |

---

## 2. 分支管理

### 2.1 分支命名规范

| 分支类型 | 命名格式 | 示例 |
|----------|----------|------|
| 主分支 | `main` | `main` |
| 功能分支 | `feature/<description>` | `feature/user-profile` |
| 修复分支 | `fix/<description>` | `fix/password-validation` |
| 文档分支 | `docs/<description>` | `docs/api-specification` |
| 测试分支 | `qa-tester` | `qa-tester` (测试报告专用) |

### 2.2 分支策略

**原则**：单一 PR 包含完整迭代的所有修改

- ✅ **推荐**：所有 Phase 修改汇总到单一分支（如 `feat/deploy-scripts`）
- ❌ **避免**：每个小修改创建独立分支

**例外**：测试报告使用独立分支（`qa-tester`），暂不合并到 main

### 2.3 Git 操作规范

```bash
# 1. 创建功能分支
git checkout -b feature/your-feature

# 2. 提交代码（小步提交）
git add .
git commit -m "feat: describe your change"

# 3. 推送到远程
git push -u origin feature/your-feature

# 4. 创建 PR
# 访问 GitHub 或使用 API 创建 PR

# 5. 合并后清理本地分支
git checkout main
git pull
git branch -d feature/your-feature
```

---

## 3. 代码审查

### 3.1 审查清单

审查人（saas-architect）必须检查以下项目：

| 审查项 | 检查内容 | 通过标准 |
|--------|----------|----------|
| **代码功能** | 功能是否实现 | 自测验证通过 |
| **代码规范** | 命名、注释、结构 | 符合项目规范 |
| **安全性** | 敏感信息、权限控制 | 无安全漏洞 |
| **测试覆盖** | 单元测试、API 测试 | 覆盖率达标 |
| **文档完整性** | README、API 文档 | 文档完整 |

### 3.2 审查流程

1. 打开 PR 链接
2. 查看 Files changed 标签页
3. 逐文件审查代码
4. 添加审查评论（问题或建议）
5. 审查完成后点击 **"Review changes"**
6. 选择 **"Approve"** 并 submit

### 3.3 审查意见模板

```markdown
## 审查结论

**状态**: ✅ 批准通过 / ❌ 需要修改

### 审查项目

| 项目 | 状态 | 备注 |
|------|------|------|
| 代码功能 | ✅ | |
| 代码规范 | ✅ | |
| 安全性 | ✅ | |
| 测试覆盖 | ✅ | |
| 文档完整性 | ✅ | |

### 建议

[如有改进建议，在此列出]

### 结论

✅ 建议合并到 main 分支
```

---

## 4. 测试要求

### 4.1 测试层级

| 层级 | 负责人 | 要求 |
|------|--------|------|
| **开发者自测** | 开发者 | 功能验证、单元测试 |
| **API 测试** | qa-tester | 核心 API 全覆盖 |
| **前端测试** | alice | 核心功能 UI 验证 |
| **生产验证** | 全体 | 部署后核心功能验证 |

### 4.2 测试报告要求

**必须包含**：
- 测试用例总数
- 通过/失败数量
- 通过率
- 失败用例分析（如适用）
- 执行环境说明（Staging/Production）

**示例**：
```markdown
## 测试结果

- 总测试：26
- 通过：21
- 失败：5
- 通过率：80.8%
- 有效功能测试：90.5% (19/21)

## 执行环境

- 环境：Staging
- URL: https://staging.plan.shujuyunxiang.com/back-server
- 时间：2026-03-19 17:24 GMT+8
```

### 4.3 测试数据管理

**重要**：测试脚本包含敏感信息（测试账号密码），**不得提交到 main 分支**

- ✅ 测试报告（脱敏）→ 提交到 `qa-tester` 分支
- ❌ 测试脚本（含密码）→ 仅 MinIO 归档，不提交 Git

---

## 5. 部署流程

### 5.1 部署前置条件

部署前必须确认：
- ✅ PR 已合并到 main 分支
- ✅ 所有测试通过（Staging 验证）
- ✅ 审查人无遗留问题
- ✅ 管理员批准部署

### 5.2 后端部署流程

```bash
# 1. 拉取最新代码
git checkout main && git pull

# 2. 构建生产版本
cd /opt/career-plan/backend
mvn clean package -DskipTests

# 3. 停止服务
bash /opt/career-plan/backend/scripts/stop.sh

# 4. 部署新 JAR
cp target/career-plan-2026-1.0.0.jar /opt/career-plan/prod/

# 5. 启动服务
bash /opt/career-plan/backend/scripts/restart.sh

# 6. 验证
curl https://plan.shujuyunxiang.com/back-server/actuator/health
```

### 5.3 前端部署流程

```bash
# 1. 构建生产版本
cd /opt/career-plan/frontend
npm run build

# 2. 部署到 Nginx
mc mirror --overwrite dist/ /www/wwwroot/plan.shujuyunxiang.com/

# 3. 验证页面访问
curl https://plan.shujuyunxiang.com
```

### 5.4 部署验证清单

部署完成后必须验证：
```
□ 服务启动成功（PID 确认）
□ 健康检查 UP（/actuator/health）
□ 登录 API 正常（3 个测试账号）
□ 数据库连接正常
□ 日志无异常报错
□ 前端页面可访问
□ 核心功能正常（密码修改/任务确认）
```

---

## 6. 经验教训

### 6.1 Phase 2/3 关键教训

| 问题 | 根因 | 解决方案 | 预防措施 |
|------|------|----------|----------|
| **PWD-004 假阳性** (3 小时浪费) | 测试数据 95 字符≠>100 字符 | 修正测试数据为 122 字符 | 测试前验证测试数据满足条件 |
| **代码不同步** (Staging≠GitHub) | 服务器直接修改未同步 Git | git-delegation 同步 59 文件 | 测试通过后立即同步 Git |
| **PR 未创建** (分支≠PR) | 仅 git push，未创建 PR | GitHub API 创建 PR #12 | push 后立即创建 PR |
| **测试脚本逻辑错误** | 检查 HTTP 状态而非 JSON code | 修改为检查 `code` 字段 | 理解后端返回格式 |
| **域名访问规则** | IP+port 测试≠真实环境 | 强制使用域名测试 | 测试环境=用户环境 |

### 6.2 必须遵守的规则

1. **测试数据验证**：调试前确认测试数据满足测试条件
2. **代码同步**：测试通过后立即同步到 Git
3. **PR 创建**：git push 后必须创建 PR
4. **域名测试**：所有测试使用域名，禁止 IP+port
5. **敏感信息**：测试脚本不提交 main 分支
6. **审查批准**：未经 saas-architect 批准不得合并
7. **部署验证**：部署后必须执行验证清单

---

## 附录 A：PR 模板

```markdown
## Summary

[简要描述本次 PR 的目的和变更]

## Changes

### 功能修复
- [列出具体修复项]

### 新增端点
- [列出新增 API]

### 部署脚本
- [列出脚本变更]

### 文档
- [列出文档变更]

## Test Results

- API 测试：[通过率]
- 前端测试：[通过率]
- 核心功能：[验证结果]

## Checklist

- [x] 代码审查通过（saas-architect）
- [x] API 测试通过（qa-tester）
- [x] 前端测试通过（alice）
```

---

## 附录 B：快速参考

| 场景 | 操作 | 负责人 |
|------|------|--------|
| 完成代码开发 | 创建分支 → 提交 → push → 创建 PR | 开发者 |
| 代码审查 | 打开 PR → 审查 → 批准 | saas-architect |
| PR 合并 | 点击 Merge | 管理员 |
| 生产部署 | 拉取 → 构建 → 部署 → 验证 | career-worker |
| 前端验证 | 访问域名 → 测试核心功能 | alice |
| API 验证 | 执行测试脚本 → 报告结果 | qa-tester |

---

**文档维护**：saas-architect  
**最后更新**：2026-03-19
