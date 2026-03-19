# 部署标准流程 (Deployment Standard)

**版本**: v1.0  
**创建时间**: 2026-03-19  
**优先级**: P0  
**适用范围**: Staging / Production 环境

---

## 🎯 核心原则

1. **直连测试优先** - 绕过 Nginx/代理，直接测试后端服务
2. **环境隔离** - 明确区分容器内/外、本地/远程
3. **日志先行** - 先看后端日志，再分析网络问题

---

## 📋 部署流程

### Step 1: 编译构建

```bash
# 1. 清理并重新编译
cd /opt/career-plan/backend
mvn clean package -DskipTests

# 2. 验证编译产物
ls -lh target/career-plan-*.jar
stat target/career-plan-*.jar  # 记录时间戳
```

### Step 2: 部署到目标环境

```bash
# 1. 复制 JAR 到部署目录
cp /opt/career-plan/backend/target/career-plan-*.jar /opt/career-plan/staging/

# 2. 验证复制成功
ls -lh /opt/career-plan/staging/career-plan-*.jar

# 3. 记录版本信息
echo "部署时间：$(date)" >> /opt/career-plan/staging/deploy.log
echo "JAR 版本：$(ls -t /opt/career-plan/staging/career-plan-*.jar | head -1)" >> /opt/career-plan/staging/deploy.log
```

### Step 3: 服务重启

```bash
# Systemd 方式（推荐）
sudo systemctl restart career-plan-staging

# 验证服务状态
sudo systemctl status career-plan-staging

# 验证端口监听
netstat -tlnp | grep 9997
```

### Step 4: **直连测试（关键！）**

```bash
# ⚠️ 必须在部署服务器上执行，绕过 Nginx！

# 1. 获取 Token（直连后端）
TOKEN=$(curl -s -X POST http://localhost:9997/back-server/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}' | jq -r '.token')

echo "Token: $TOKEN"

# 2. 测试核心 API（直连后端）
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:9997/back-server/api/v1/progress/summary

# 预期：200 OK + {"reports":[],"total":0}

# 3. 查看后端日志（关键诊断）
tail -100 /opt/career-plan/staging/app-9997.log | grep -E 'JWT|Username|Roles|400|403|progress'
```

### Step 5: Nginx 验证（仅在直连测试通过后）

```bash
# 1. 确认 Nginx 配置
cat /www/server/panel/vhost/nginx/staging.plan.shujuyunxiang.com.conf | grep -A15 "location /back-server"

# 2. 测试域名访问
curl -H "Authorization: Bearer $TOKEN" \
  https://staging.plan.shujuyunxiang.com/back-server/api/v1/progress/summary

# 3. 对比直连和域名访问结果
# 如果直连成功但域名失败 → Nginx 问题
# 如果直连失败 → 后端问题（优先排查后端）
```

---

## 🔍 问题排查流程

### 场景 1: 直连失败

```
症状：curl http://localhost:9997/... 返回 400/403/500
排查：
1. 查看后端日志：tail -100 /opt/career-plan/staging/app-9997.log
2. 检查服务状态：systemctl status career-plan-staging
3. 检查端口监听：netstat -tlnp | grep 9997
4. 检查 JAR 版本：ls -lh /opt/career-plan/staging/*.jar
```

### 场景 2: 直连成功，域名失败

```
症状：本地直连 200 OK，域名访问 400/403
排查：
1. 检查 Nginx 配置：grep -A15 "location /back-server" nginx.conf
2. 确认 Authorization 转发：proxy_set_header Authorization $http_authorization;
3. 检查 Nginx 日志：tail -100 /var/log/nginx/error.log
4. 重启 Nginx：nginx -s reload
```

### 场景 3: 服务无法启动

```
症状：systemctl start 失败
排查：
1. 查看系统日志：journalctl -u career-plan-staging -n 50
2. 检查端口占用：netstat -tlnp | grep 9997
3. 检查 JAR 完整性：jar tf career-plan-*.jar | head
4. 检查 Java 版本：java -version
```

---

## ✅ 部署检查清单

### 部署前

- [ ] 代码已审查合并
- [ ] JAR 文件已构建（`mvn clean package`）
- [ ] JAR 时间戳验证（确保是最新编译产物）
- [ ] 数据库备份完成（Production）

### 部署中

- [ ] 旧服务已停止
- [ ] 新 JAR 已复制
- [ ] 服务已重启
- [ ] 端口监听正常（`netstat -tlnp | grep 9997`）

### 部署后（关键！）

- [ ] **直连测试通过**（`curl http://localhost:9997/...`）
- [ ] 健康检查通过（`/actuator/health` → UP）
- [ ] 登录 API 测试通过（返回 JWT Token）
- [ ] 核心 API 测试通过（Progress/Plan/Task）
- [ ] 后端日志无异常（无 400/403/500 错误）
- [ ] **直连测试通过后**，再验证域名访问

---

## 📝 变更记录

| 日期 | 变更 | 作者 |
|------|------|------|
| 2026-03-19 | 初始版本 - 强调直连测试优先 | saas-architect |

---

## 🔗 相关文档

- `TEST-ISOLATION-STANDARD.md` - 测试环境隔离规范
- `INTEGRATION-DEBUG-STANDARD.md` - 联调排查标准流程
- `DEPLOYMENT-CHECKLIST.md` - 部署检查清单
