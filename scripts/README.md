# 构建与部署脚本使用说明

## 目录

- [环境准备](#环境准备)
- [脚本清单](#脚本清单)
- [使用方法](#使用方法)
- [部署流程](#部署流程)
- [故障排查](#故障排查)

## 环境准备

### 1. SSH 密钥配置

确保 SSH 私钥已存储在 `~/.ssh/id_ed25519`：

```bash
ls -la ~/.ssh/id_ed25519
# 权限应该是 600
chmod 600 ~/.ssh/id_ed25519
```

### 2. 测试 SSH 连接

```bash
ssh -i ~/.ssh/id_ed25519 root@47.115.63.159
```

### 3. Maven 环境

确保已安装 Maven 3.6+：

```bash
mvn -version
```

## 脚本清单

| 脚本 | 用途 | 环境 |
|------|------|------|
| `build-staging.sh` | 构建 Staging 版本 | 本地 |
| `deploy-staging.sh` | 部署到 Staging | 本地 → 服务器 |
| `build-prod.sh` | 构建 Production 版本 | 本地 |
| `deploy-prod.sh` | 部署到 Production | 本地 → 服务器 |

## 使用方法

### Staging 环境

#### 1. 构建

```bash
cd /root/tasks/career-plan-2026/scripts
./build-staging.sh
```

**输出示例**：
```
======================================
🔨 Staging 环境构建开始
======================================
📦 执行 Maven 构建...
[INFO] BUILD SUCCESS
✅ JAR 文件大小：75M
======================================
✅ Staging 构建完成
📦 JAR 文件：../backend/target/career-plan-2026-1.0.0.jar
======================================
```

#### 2. 部署

```bash
./deploy-staging.sh
```

**部署流程**：
1. 测试 SSH 连接
2. 停止旧服务
3. 验证端口已释放
4. 上传 JAR 文件
5. 启动新服务
6. 健康检查
7. 登录测试

**预期输出**：
```
======================================
🚀 Staging 环境部署开始
======================================
🔐 测试 SSH 连接...
✅ SSH 连接成功
🛑 停止旧服务...
✅ 端口 9997 已释放
📤 上传 JAR 文件...
🔥 启动新服务...
⏳ 等待服务启动（25 秒）...
🏥 健康检查...
✅ 健康检查通过：{"status":"UP"}
🔐 登录测试...
✅ 登录测试通过
======================================
✅ Staging 部署完成
🌐 访问地址：https://staging.plan.shujuyunxiang.com/back-server
======================================
```

### Production 环境

#### 1. 构建

```bash
./build-prod.sh
```

#### 2. 部署

```bash
./deploy-prod.sh
```

**注意**：生产部署有 5 秒确认延迟，可按 Ctrl+C 取消。

## 部署流程

### 完整流程（推荐）

```bash
# Staging 环境
./build-staging.sh    # 1. 构建
./deploy-staging.sh   # 2. 部署
# 等待测试团队验证

# Production 环境（验证通过后）
./build-prod.sh       # 3. 构建
./deploy-prod.sh      # 4. 部署
```

### 快速部署（仅部署，不重新构建）

```bash
# 使用已有的 JAR 文件直接部署
cd scripts
./deploy-staging.sh
```

## 验证清单

### 部署后必须验证

- [ ] 健康检查通过
  ```bash
  curl https://staging.plan.shujuyunxiang.com/back-server/actuator/health
  ```

- [ ] 登录 API 正常
  ```bash
  curl -X POST https://staging.plan.shujuyunxiang.com/back-server/api/v1/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"123456"}'
  ```

- [ ] 所有测试账号登录成功
  ```bash
  # admin/123456
  # testuser/test123
  # advisor1/Advisor1234!
  ```

- [ ] 未授权访问返回 401
  ```bash
  curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/profile \
    -H "Content-Type: application/json" \
    -d '{"name":"test"}'
  # 期望：{"code":401,"message":"未授权访问"}
  ```

## 故障排查

### 问题 1: SSH 连接失败

**症状**：
```
❌ SSH 连接失败
```

**解决方案**：
```bash
# 1. 检查 SSH 密钥权限
chmod 600 ~/.ssh/id_ed25519

# 2. 测试连接
ssh -i ~/.ssh/id_ed25519 -v root@47.115.63.159

# 3. 检查服务器是否可达
ping 47.115.63.159
```

### 问题 2: 端口未释放

**症状**：
```
❌ 端口 9997 仍被占用
```

**解决方案**：
```bash
# SSH 到服务器手动清理
ssh -i ~/.ssh/id_ed25519 root@47.115.63.159

# 查找占用进程
lsof -i :9997

# 杀死进程
kill -9 <PID>

# 验证端口已释放
lsof -i :9997
```

### 问题 3: 健康检查失败

**症状**：
```
❌ 健康检查失败：{"status":"DOWN"}
```

**解决方案**：
```bash
# SSH 到服务器查看日志
ssh -i ~/.ssh/id_ed25519 root@47.115.63.159

# 查看应用日志
tail -100 /opt/career-plan/staging/app.log

# 查找错误
grep -i "error\|exception" /opt/career-plan/staging/app.log
```

### 问题 4: JAR 文件不存在

**症状**：
```
❌ JAR 文件不存在：../backend/target/career-plan-2026-1.0.0.jar
```

**解决方案**：
```bash
# 先执行构建
./build-staging.sh
# 或
./build-prod.sh
```

### 问题 5: 登录测试失败

**症状**：
```
❌ 登录测试失败：{"code":400,"message":"用户名或密码错误"}
```

**解决方案**：
```bash
# SSH 到服务器重置密码
ssh -i ~/.ssh/id_ed25519 root@47.115.63.159

# 使用 Python 生成正确的 bcrypt 哈希
python3 -c "import bcrypt; print(bcrypt.hashpw('123456'.encode(), bcrypt.gensalt()).decode())"

# 更新数据库
mysql -u root -p career_plan_staging
UPDATE users SET password = '<哈希值>' WHERE username = 'admin';
```

## 回滚流程

### Staging 环境回滚

```bash
ssh -i ~/.ssh/id_ed25519 root@47.115.63.159

cd /opt/career-plan/staging

# 列出备份
ls -lh career-plan-2026-1.0.0.jar.backup.*

# 恢复备份
cp career-plan-2026-1.0.0.jar.backup.YYYYMMDD-HHMMSS career-plan-2026-1.0.0.jar

# 重启服务
pkill -9 java
sleep 3
nohup java -Xms512m -Xmx2g -jar career-plan-2026-1.0.0.jar \
  --spring.profiles.active=staging > app.log 2>&1 &
```

## 修订历史

| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| 1.0 | 2026-03-19 | career-worker | 初始版本 |
