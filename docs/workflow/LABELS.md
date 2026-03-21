# GitHub Issue 标签系统

**项目**: career-plan-2026  
**最后更新**: 2026-03-21  
**维护者**: qa-tester

---

## 📋 标签分类

### 🔴 优先级标签 (Priority)

| 标签 | 颜色 | 说明 | 响应时间 |
|------|------|------|---------|
| `P0-critical` | #B60205 | 严重：系统崩溃、数据丢失、核心功能不可用 | 立即 |
| `P1-high` | #D93F0B | 高：主要功能受损，有替代方案 | 24 小时内 |
| `P2-medium` | #FBCA04 | 中：功能部分受损，影响用户体验 | 3 天内 |
| `P3-low` | #0E8A16 | 低：轻微问题，不影响主要功能 | 1 周内 |

### 🏷️ 类型标签 (Type)

| 标签 | 颜色 | 说明 |
|------|------|------|
| `bug` | #D93F0B | 确认的 Bug |
| `enhancement` | #A2EEEF | 新功能或改进 |
| `documentation` | #0075CA | 文档相关 |
| `question` | #D876E3 | 需要更多信息或澄清 |
| `discussion` | #C5DEF5 | 需要团队讨论 |

### 📊 状态标签 (Status)

| 标签 | 颜色 | 说明 |
|------|------|------|
| `triage` | #E4E669 | 待分类：新 Issue 等待初步审查 |
| `confirmed` | #0E8A16 | 已确认：问题已验证存在 |
| `in-progress` | #006B75 | 进行中：开发者正在处理 |
| `review-needed` | #D93F0B | 待审查：修复完成等待 QA 验证 |
| `verified` | #0E8A16 | 已验证：QA 验证通过 |
| `blocked` | #B60205 | 被阻塞：依赖其他任务 |
| `wontfix` | #FFFFFF | 不修复：决定不处理 |
| `duplicate` | #CFD3D7 | 重复：已有相同 Issue |

### 🎯 模块标签 (Module)

| 标签 | 颜色 | 说明 |
|------|------|------|
| `frontend` | #0075CA | 前端问题 |
| `backend` | #0E8A16 | 后端 API 问题 |
| `database` | #D93F0B | 数据库相关 |
| `devops` | #6F42C1 | 部署/CI/CD相关 |
| `security` | #B60205 | 安全问题 |
| `performance` | #FBCA04 | 性能问题 |
| `ui-ux` | #A2EEEF | 界面/用户体验 |

### 🧪 测试标签 (Testing)

| 标签 | 颜色 | 说明 |
|------|------|------|
| `test-case` | #0E8A16 | 需要添加测试用例 |
| `regression` | #D93F0B | 回归测试发现 |
| `e2e` | #006B75 | E2E 测试相关 |
| `api-test` | #A2EEEF | API 测试相关 |

---

## 📐 标签使用规范

### 新 Issue 创建时

1. **必须标签**：
   - 至少 1 个优先级标签 (`P0-critical` ~ `P3-low`)
   - 至少 1 个类型标签 (`bug`, `enhancement`, etc.)
   - `triage` (自动添加，等待审查)

2. **推荐标签**：
   - 至少 1 个模块标签 (标识问题所属模块)

### Issue 处理流程中的标签变化

```
新建 Issue
  ↓ [自动添加 triage]
待分类 (triage)
  ↓ [Team Lead 审查]
已确认 (confirmed) + 优先级 + 模块
  ↓ [分配开发者]
进行中 (in-progress)
  ↓ [开发者完成修复]
待审查 (review-needed)
  ↓ [QA 验证]
已验证 (verified) → 关闭
  或
被阻塞 (blocked) → 等待依赖
```

### 标签组合示例

| 场景 | 标签组合 |
|------|---------|
| 严重 Bug | `P0-critical` + `bug` + `backend` + `confirmed` |
| 新功能请求 | `P2-medium` + `enhancement` + `frontend` + `triage` |
| 性能优化 | `P1-high` + `enhancement` + `performance` + `in-progress` |
| 文档错误 | `P3-low` + `documentation` + `confirmed` |
| 等待 QA 验证 | `review-needed` + `bug` + `P2-medium` |

---

## 🔧 标签管理

### 创建新标签

1. 进入 GitHub 仓库 → Issues → Labels
2. 点击 "New label"
3. 填写名称、颜色、描述
4. 保存

### 批量导入标签

使用 GitHub API 或 CLI 工具批量创建：

```bash
# 使用 gh CLI
gh label create "P0-critical" --color "B60205" --description "严重：系统崩溃、数据丢失"
gh label create "P1-high" --color "D93F0B" --description "高优先级"
# ... 其他标签
```

### 标签审查

- **频率**: 每月审查一次标签使用情况
- **负责人**: Team Lead + qa-tester
- **检查项**:
  - 是否有 Issue 缺少优先级标签
  - 是否有 Issue 状态标签不正确
  - 是否需要新增或删除标签

---

## 📊 标签统计与报告

### 每周标签报告

qa-tester 每周一生成标签统计报告：

```markdown
## 上周 Issue 统计

| 优先级 | 新增 | 已解决 | 待处理 |
|--------|------|--------|--------|
| P0 | X | X | X |
| P1 | X | X | X |
| P2 | X | X | X |
| P3 | X | X | X |

| 模块 | 新增 | 已解决 |
|------|------|--------|
| frontend | X | X |
| backend | X | X |
```

### 标签使用分析

- **趋势分析**: 哪些模块 Bug 最多？
- **优先级分布**: P0/P1 占比是否合理？
- **解决周期**: 各优先级平均解决时间

---

## 🎓 团队培训要点

### 新成员入职

1. 阅读本标签系统文档
2. 了解各标签含义和使用场景
3. 练习创建 Issue 并正确添加标签
4. 了解 Issue 处理流程中的标签变化

### 最佳实践

- ✅ 创建 Issue 时立即添加优先级和类型标签
- ✅ 状态变化时及时更新状态标签
- ✅ 避免给单个 Issue 添加过多标签（建议不超过 5 个）
- ✅ 关闭 Issue 前确认状态标签为 `verified`

---

**维护记录**：
- 2026-03-21: 初始版本创建 (qa-tester)
