# Career Plan 2026 - 后端部署和测试指南

## 📋 部署步骤

### 1. SSH 登录 ECS 服务器

```bash
ssh root@47.115.63.159
# SSH 凭证已通过安全渠道存储，请联系 manager 获取
```

### 2. 更新代码

```bash
cd /opt/career-plan
git pull origin feature/backend-dev
```

### 3. 重新构建

```bash
cd backend
mvn clean package -DskipTests
cp target/*.jar ../career-plan.jar
cd ..
```

### 4. 重启应用

```bash
# 停止旧进程
pkill -f career-plan
sleep 3

# 启动新应用（强制 IPv4）
java -Djava.net.preferIPv4Stack=true -jar career-plan.jar > app.log 2>&1 &
echo $! > app.pid

# 等待启动
sleep 30
```

### 5. 验证启动

```bash
# 检查进程
ps aux | grep career-plan

# 检查端口
netstat -tlnp | grep 9999

# 查看日志
tail -50 app.log
```

---

## 🧪 测试登录功能

### 方法 1: 使用测试脚本（推荐）

```bash
cd /opt/career-plan
chmod +x backend/scripts/test-login.sh
./backend/scripts/test-login.sh
```

### 方法 2: 手动测试

```bash
# 1. 健康检查
curl -m 5 http://[::1]:9999/actuator/health

# 2. 测试登录
curl -m 10 -X POST http://[::1]:9999/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 3. 使用 Token 测试任务接口
# (假设返回的 token 是 eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...)
curl -m 10 -X GET http://[::1]:9999/api/v1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🔧 故障排查

### 问题 1: 登录返回 401

**可能原因**: 数据库中密码哈希不正确

**解决方案**:
```bash
mysql -u root -p
# MySQL 凭证已通过安全渠道存储

USE career_plan;
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' 
WHERE username = 'admin';

SELECT id, username, LEFT(password, 30) FROM users WHERE username = 'admin';
EXIT;
```

然后重启应用并重新测试。

### 问题 2: 应用启动失败

**查看日志**:
```bash
tail -200 app.log
```

**常见错误**:
- 端口被占用：`pkill -f career-plan` 后重启
- 数据库连接失败：检查 MySQL 是否运行
- 配置错误：检查 `application.yml`

### 问题 3: 请求超时

**检查防火墙**:
```bash
# 临时关闭防火墙
systemctl stop firewalld

# 或添加规则
firewall-cmd --permanent --add-port=9999/tcp
firewall-cmd --reload
```

**检查 Nginx 配置**:
```bash
systemctl status nginx
# 如果 Nginx 配置了反向代理，确保配置正确
```

---

## 📊 预期结果

### 健康检查
```json
{"status":"UP","groups":["liveness","readiness"]}
```

### 登录成功
```json
{
  "code": 200,
  "message": "登录成功",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000
}
```

### 登录失败
```json
{
  "code": 401,
  "message": "用户不存在"  // 或 "密码错误"
}
```

---

## 📞 联系方式

如有问题，请在项目群中反馈：
- **项目房间**: `!ICKn4EvhynWY4K5cgC:matrix-local.hiclaw.io:18080`
- **@manager**: @manager:matrix-local.hiclaw.io:18080
- **@backend-dev**: @backend-dev:matrix-local.hiclaw.io:18080

---

**Good Luck! 🚀**
