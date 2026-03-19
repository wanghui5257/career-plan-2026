# 联调排查标准流程 (Integration Debug Standard)

**版本**: v1.0  
**创建时间**: 2026-03-19  
**优先级**: P0  
**适用范围**: 前后端联调 / API 测试 / 问题排查

---

## 🎯 核心原则

1. **日志先行** - 先看后端日志，再分析网络
2. **直连优先** - 绕过 Nginx/代理，直接测试后端
3. **逐层隔离** - 从内到外逐层验证，定位问题边界
4. **避免重复** - 已验证的问题不再重复排查

---

## 📋 排查流程

### Step 1: 确认问题现象

```
问题描述：
- 测试时间：{time}
- 测试命令：{command}
- 预期结果：{expected}
- 实际结果：{actual}
- 错误信息：{error}
```

### Step 2: 查看后端日志（关键！）

```bash
# 1. 查看最近请求日志
tail -200 /opt/career-plan/staging/app-9997.log

# 2. 搜索关键错误
grep -E '400|403|500|JWT|Username|Roles|SecurityContext' /opt/career-plan/staging/app-9997.log | tail -50

# 3. 查看特定时间范围
grep '2026-03-19T10:40' /opt/career-plan/staging/app-9997.log
```

**日志分析要点**：
- ✅ 请求是否到达后端（有日志记录）
- ✅ JWT Token 是否提取成功
- ✅ 角色信息是否正确
- ✅ SecurityContext 是否设置
- ✅ 是否有异常堆栈

### Step 3: 直连测试（绕过 Nginx）

```bash
# ⚠️ 在部署服务器上执行！

# 1. 获取新 Token
TOKEN=$(curl -s -X POST http://localhost:9997/back-server/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}' | jq -r '.token')

echo "Token: $TOKEN"

# 2. 解码 Token 查看 payload
echo "$TOKEN" | cut -d'.' -f2 | base64 -d 2>/dev/null | jq .

# 3. 测试问题 API
curl -v -H "Authorization: Bearer $TOKEN" \
  http://localhost:9997/back-server/api/v1/progress/summary

# 4. 查看后端日志（确认请求记录）
tail -50 /opt/career-plan/staging/app-9997.log | grep -E 'JWT|progress|400'
```

**诊断逻辑**：
| 直连结果 | 域名结果 | 根因定位 |
|---------|---------|---------|
| ✅ 成功 | ✅ 成功 | 问题已解决 |
| ✅ 成功 | ❌ 失败 | Nginx/网络问题 |
| ❌ 失败 | ❌ 失败 | **后端问题（优先排查）** |

### Step 4: 根据日志定位根因

#### 场景 1: 日志显示 "No JWT found"

```
根因：请求未携带 Token 或 Header 丢失
排查：
1. 确认请求携带 Authorization Header
2. 检查 Token 格式：Bearer {token}
3. 检查 Nginx 是否转发 Authorization（仅当直连成功时）
```

#### 场景 2: 日志显示 "AnonymousAuthenticationFilter"

```
根因：JWT 过滤器顺序错误，认证被 Anonymous 覆盖
排查：
1. 检查 SecurityConfig 过滤器顺序
2. 确认禁用 Anonymous：`.anonymous().disable()`
3. 重新编译部署：`mvn clean package`
4. 重启服务：`systemctl restart career-plan-staging`
```

#### 场景 3: 日志显示 "400 Access Denied"

```
根因：权限验证失败
排查：
1. 检查 Token 中 roles claim：解码 Token 查看
2. 检查 @PreAuthorize 注解格式
3. 检查角色前缀一致性（统一无前缀）
4. 查看后端日志中的角色信息
```

#### 场景 4: 日志无请求记录

```
根因：请求未到达后端
排查：
1. 检查服务是否运行：`systemctl status career-plan-staging`
2. 检查端口监听：`netstat -tlnp | grep 9997`
3. 检查 Nginx 配置（仅当直连成功时）
```

### Step 5: 修复验证

```bash
# 1. 修复后重新编译
cd /opt/career-plan/backend
mvn clean package -DskipTests

# 2. 部署新 JAR
cp target/career-plan-*.jar /opt/career-plan/staging/

# 3. 重启服务
systemctl restart career-plan-staging

# 4. 直连验证（关键！）
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:9997/back-server/api/v1/progress/summary

# 5. 查看后端日志确认
tail -50 /opt/career-plan/staging/app-9997.log | grep -E 'JWT|progress|200'
```

---

## 🚨 避免重复排查

### 已验证的问题（不再重复）

| 问题 | 验证时间 | 验证方式 | 状态 |
|------|---------|---------|------|
| Nginx Authorization 转发 | 2026-03-18 | 管理员确认 | ✅ 已配置 |
| 后端直连测试 | 每次部署后 | `curl http://localhost:9997/...` | ✅ 必须执行 |
| SecurityConfig 过滤器顺序 | 2026-03-19 10:31 | 日志分析 | ✅ 已修复 |

### 排查前确认清单

- [ ] 是否已查看后端日志？
- [ ] 是否已执行直连测试？
- [ ] 是否是重复问题（检查上方表格）？
- [ ] 是否已确认服务运行状态？

---

## 📝 排查报告模板

```markdown
# 问题排查报告

**问题**: {简要描述}  
**时间**: {发现时间}  
**排查人**: {name}

## 问题现象

```
测试命令：{command}
预期结果：{expected}
实际结果：{actual}
错误信息：{error}
```

## 排查过程

| 步骤 | 操作 | 结果 | 说明 |
|------|------|------|------|
| 1 | 查看后端日志 | {result} | {note} |
| 2 | 直连测试 | {result} | {note} |
| 3 | {step} | {result} | {note} |

## 根因分析

```
{详细分析}
```

## 修复方案

```
{修复命令/代码}
```

## 验证结果

```
{验证命令和结果}
```

## 经验总结

```
{避免重复的建议}
```
```

---

## ✅ 排查检查清单

### 排查前

- [ ] 确认问题现象（测试命令、预期、实际）
- [ ] 确认排查环境（容器内/外、本地/远程）
- [ ] 确认是否是重复问题（检查已验证列表）

### 排查中

- [ ] **优先查看后端日志**
- [ ] **优先执行直连测试**
- [ ] 记录每步排查结果
- [ ] 避免重复已验证的步骤

### 排查后

- [ ] 记录根因和修复方案
- [ ] 更新已验证问题列表
- [ ] 同步团队避免重复

---

## 📝 变更记录

| 日期 | 变更 | 作者 |
|------|------|------|
| 2026-03-19 | 初始版本 - 强调日志先行、直连优先 | saas-architect |

---

## 🔗 相关文档

- `DEPLOYMENT-STANDARD.md` - 部署标准流程
- `TEST-ISOLATION-STANDARD.md` - 测试环境隔离规范
- `DEPLOYMENT-CHECKLIST.md` - 部署检查清单
