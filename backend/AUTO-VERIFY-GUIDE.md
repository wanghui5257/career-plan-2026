# Career Plan 2026 - 后端自验证指南

## 🎯 目标

在 ECS 服务器上自动验证登录功能，无需人工干预。

---

## 🚀 自动验证脚本

### 步骤 1: SSH 登录 ECS

```bash
ssh root@47.115.63.159
<!-- SSH 凭证已通过安全渠道存储，请联系 manager 获取 -->
```

### 步骤 2: 运行自动验证脚本

```bash
cd /opt/career-plan
chmod +x backend/scripts/auto-verify.sh
./backend/scripts/auto-verify.sh
```

**脚本会自动**:
1. ✅ 检查应用是否运行
2. ✅ 检查端口是否监听
3. ✅ 测试健康检查
4. ✅ 生成新的 BCrypt 哈希
5. ✅ 显示 SQL 更新语句
6. ✅ 测试当前密码验证

### 步骤 3: 执行返回的 SQL

脚本会输出类似：
```sql
UPDATE users SET password = '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' WHERE username = 'admin';
```

**执行**:
```bash
mysql -u root -p
# 输入 MySQL root 密码

USE career_plan;
# 复制脚本输出的 UPDATE 语句并执行

SELECT id, username, LEFT(password, 30) FROM users WHERE username = 'admin';
EXIT;
```

### 步骤 4: 重新测试登录

```bash
./backend/scripts/test-login.sh
```

**预期结果**:
```
✅ 应用正在运行
✅ 端口 9999 正在监听
✅ 健康检查通过
✅ 登录成功！
   Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
✅ 任务接口测试通过
```

---

## 🔧 手动验证方案

### 方案 A: 使用测试接口

```bash
# 1. 生成新哈希
curl -m 5 "http://[::1]:9999/api/v1/test/hash?password=admin123"

# 2. 验证当前哈希
curl -m 5 "http://[::1]:9999/api/v1/test/verify?password=admin123&hash=\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
```

### 方案 B: 直接更新数据库

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 生成新哈希（在 MySQL 中）
# 注意：MySQL 没有内置 BCrypt，需要应用生成

# 3. 使用应用生成的哈希
USE career_plan;
UPDATE users SET password = '从/api/v1/test/hash 接口获取' WHERE username = 'admin';

# 4. 验证
SELECT id, username, password FROM users WHERE username = 'admin';
EXIT;
```

---

## 📊 常见问题

### 问题 1: 测试接口无返回

**原因**: 应用可能未启动或端口未监听

**解决**:
```bash
# 检查进程
ps aux | grep career-plan

# 检查端口
netstat -tlnp | grep 9999

# 查看日志
tail -100 app.log

# 重启应用
pkill -f career-plan
java -Djava.net.preferIPv4Stack=true -jar career-plan.jar > app.log 2>&1 &
```

### 问题 2: BCrypt 验证失败

**原因**: 哈希值格式不兼容

**解决**: 使用 `/api/v1/test/hash` 接口生成 Spring Security 兼容的哈希值

---

## 📞 联系方式

如有问题，请在项目群中反馈：
- **项目房间**: `!ICKn4EvhynWY4K5cgC:matrix-local.hiclaw.io:18080`
- **@manager**: @manager:matrix-local.hiclaw.io:18080
- **@backend-dev**: @backend-dev:matrix-local.hiclaw.io:18080

---

**Good Luck! 🚀**
