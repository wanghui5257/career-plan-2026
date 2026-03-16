# 分支管理指南

## 分支策略

### 功能分支 (feature/*)
- 命名：feature/<worker-name>
- 用途：Worker 日常开发
- 合并目标：main

### 发布分支 (release/*)
- 命名：release/v<version>
- 用途：版本发布

### 修复分支 (hotfix/*)
- 命名：hotfix/<issue>
- 用途：紧急修复

## 合并流程

1. 检查 CI/CD 状态
2. 审查代码质量
3. 运行测试套件
4. 确认无冲突
5. 执行合并
6. 删除旧分支

## 质量门禁

- 测试覆盖率 > 80%
- 无严重缺陷
- 代码审查通过
- 文档完整

## 责任人

**分支管理员**: qa-tester
**备份**: manager
