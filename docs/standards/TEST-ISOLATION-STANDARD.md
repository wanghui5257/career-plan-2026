# 测试环境隔离规范 (Test Isolation Standard)

**版本**: v1.0  
**创建时间**: 2026-03-19  
**优先级**: P0  
**适用范围**: 单元测试 / 集成测试 / 联调测试

---

## 🎯 核心原则

1. **直连优先** - 优先测试后端直连，排除网络/代理干扰
2. **环境明确** - 明确区分容器内/外、本地/远程
3. **逐层隔离** - 从内到外逐层验证，定位问题边界

---

## 📋 测试层级

```
┌─────────────────────────────────────────┐
│  Layer 4: 域名访问 (Nginx + HTTPS)       │  ← 最后验证
├─────────────────────────────────────────┤
│  Layer 3: 容器网络 (宿主机 → 容器)        │  ← 直连测试
├─────────────────────────────────────────┤
│  Layer 2: 容器内 (localhost:9997)       │  ← 优先验证
├─────────────────────────────────────────┤
│  Layer 1: 单元测试 (JUnit)              │  ← 开发阶段
└─────────────────────────────────────────┘
```

---

## 🔍 逐层隔离测试流程

### Layer 1: 单元测试（开发阶段）

```bash
# 在开发环境执行
cd /opt/career-plan/backend
mvn test

# 验证：所有单元测试通过
```

### Layer 2: 容器内直连（部署后优先验证）

```bash
# ⚠️ 在部署服务器上执行（容器内或宿主机直连容器）

# 1. 获取 Token
TOKEN=$(curl -s -X POST http://localhost:9997/back-server/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}' | jq -r '.token')

# 2. 测试核心 API
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:9997/back-server/api/v1/progress/summary

# 3. 查看后端日志
tail -100 /opt/career-plan/staging/app-9997.log

# ✅ 通过标准：200 OK + 日志无异常
```

### Layer 3: 容器网络（宿主机 → 容器）

```bash
# 在宿主机执行（非容器内）

# 1. 确认容器端口映射
docker ps | grep career-plan

# 2. 测试容器网络连通性
curl http://127.0.0.1:9997/back-server/actuator/health

# ✅ 通过标准：{"status":"UP"}
```

### Layer 4: 域名访问（最后验证）

```bash
# ⚠️ 仅在 Layer 2/3 通过后执行！

# 1. 测试域名访问
curl -H "Authorization: Bearer $TOKEN" \
  https://staging.plan.shujuyunxiang.com/back-server/api/v1/progress/summary

# 2. 对比直连结果
# 如果 Layer 2 成功但 Layer 4 失败 → Nginx/网络问题
# 如果 Layer 2 失败 → 后端问题（优先排查后端）
```

---

## 🚨 常见错误诊断

### 错误 1: Layer 2 失败（直连失败）

```
症状：curl http://localhost:9997/... 返回 400/403/500
根因：后端代码/配置问题
排查：
1. 查看后端日志：tail -100 app-9997.log
2. 检查服务状态：systemctl status career-plan-staging
3. 检查 JAR 版本：ls -lh staging/*.jar
4. 检查编译产物：确认新代码已部署
```

### 错误 2: Layer 2 成功，Layer 4 失败

```
症状：直连 200 OK，域名访问 400/403
根因：Nginx/网络问题
排查：
1. 检查 Nginx 配置：grep -A15 "location /back-server" nginx.conf
2. 确认 Authorization 转发：proxy_set_header Authorization $http_authorization;
3. 检查 Nginx 日志：tail -100 /var/log/nginx/error.log
4. 重启 Nginx：nginx -s reload
```

### 错误 3: Layer 3 失败（容器网络不通）

```
症状：宿主机无法访问容器端口
根因：容器网络/端口映射问题
排查：
1. 检查容器状态：docker ps
2. 检查端口映射：docker port <container_id>
3. 检查防火墙：iptables -L -n
4. 重启容器：docker restart <container_id>
```

---

## ✅ 测试检查清单

### 开发阶段

- [ ] 单元测试通过（`mvn test`）
- [ ] 集成测试通过（`mvn verify`）
- [ ] 代码审查通过（PR 合并）

### 部署阶段

- [ ] **Layer 2 直连测试通过**（`curl http://localhost:9997/...`）
- [ ] 健康检查通过（`/actuator/health` → UP）
- [ ] 登录 API 测试通过（返回 JWT Token）
- [ ] 后端日志无异常

### 验证阶段

- [ ] Layer 3 容器网络测试通过
- [ ] **Layer 2 通过后**，再执行 Layer 4 域名访问
- [ ] 对比直连和域名访问结果
- [ ] 记录测试结果到日志

---

## 📝 测试报告模板

```markdown
# 测试报告

**版本**: {version}  
**日期**: {date}  
**环境**: {staging/production}

## 测试结果

| 层级 | 测试项 | 结果 | 说明 |
|------|--------|------|------|
| Layer 1 | 单元测试 | ✅ 通过 | 50/50 |
| Layer 2 | 直连测试 | ✅ 通过 | 200 OK |
| Layer 3 | 容器网络 | ✅ 通过 | 端口映射正常 |
| Layer 4 | 域名访问 | ✅ 通过 | 200 OK |

## 问题记录

| 问题 | 根因 | 修复 | 状态 |
|------|------|------|------|
| Progress API 400 | 过滤器顺序问题 | 禁用 Anonymous | ✅ 已修复 |
```

---

## 📝 变更记录

| 日期 | 变更 | 作者 |
|------|------|------|
| 2026-03-19 | 初始版本 - 强调逐层隔离 | saas-architect |

---

## 🔗 相关文档

- `DEPLOYMENT-STANDARD.md` - 部署标准流程
- `INTEGRATION-DEBUG-STANDARD.md` - 联调排查标准流程
- `TEST-VALIDATION-STANDARD.md` - 测试验证标准
