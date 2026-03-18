# 后端部署流程

## 目的

规范后端服务部署流程，确保部署安全、可靠、可回滚，避免 Phase 2 中遇到的服务崩溃问题。

## 适用范围

- 后端开发人员
- DevOps 人员
- 运维人员

## 环境说明

| 环境 | 域名 | 端口 | 用途 |
|------|------|------|------|
| Staging | `staging.plan.shujuyunxiang.com` | 9997 | 测试验证 |
| Production | `plan.shujuyunxiang.com` | 9999 | 生产环境 |

## 部署前准备

### 1. 代码准备

```bash
# 切换到主分支
git checkout main

# 拉取最新代码
git pull origin main

# 确认代码状态
git status
git log -5
```

### 2. 构建验证

```bash
# 进入后端目录
cd backend

# 清理并构建
mvn clean package -DskipTests

# 验证构建产物
ls -lh target/career-plan-2026-1.0.0.jar

# 本地测试（可选）
java -jar target/career-plan-2026-1.0.0.jar --spring.profiles.active=staging
```

### 3. 配置检查

**必须检查的配置文件**：

```yaml
# application-staging.yml
jwt:
  secret: <从密钥管理获取>
  expiration: 86400000

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/career_plan_staging
    username: root
    password: <从密钥管理获取>
    
  security:
    enabled: false  # Staging 环境禁用

server:
  port: 9997
```

```yaml
# application-production.yml
jwt:
  secret: <从密钥管理获取>
  expiration: 86400000

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/career_plan_production
    username: root
    password: <从密钥管理获取>
    
  security:
    enabled: true  # 生产环境必须启用

server:
  port: 9999
```

## 部署流程

### Staging 环境部署

#### 步骤 1: SSH 连接服务器

```bash
ssh -i <密钥路径> root@47.115.63.159
```

#### 步骤 2: 备份当前服务

```bash
# 进入部署目录
cd /opt/career-plan/staging

# 备份当前 JAR
cp career-plan-2026-1.0.0.jar career-plan-2026-1.0.0.jar.backup.$(date +%Y%m%d-%H%M%S)

# 备份日志
cp app.log app.log.backup.$(date +%Y%m%d-%H%M%S)
```

#### 步骤 3: 停止旧服务

```bash
# 查找进程
ps aux | grep career-plan

# 停止服务
pkill -9 java

# 确认已停止
sleep 3
ps aux | grep java

# 确认端口已释放
lsof -i :9997
```

#### 步骤 4: 上传新 JAR

```bash
# 从本地上传
scp -i <密钥路径> backend/target/career-plan-2026-1.0.0.jar \
    root@47.115.63.159:/opt/career-plan/staging/

# 验证文件大小（应该约 75MB）
ls -lh /opt/career-plan/staging/career-plan-2026-1.0.0.jar
```

#### 步骤 5: 启动新服务

```bash
cd /opt/career-plan/staging

nohup java -Xms512m -Xmx2g \
  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/opt/career-plan/staging/heapdump.hprof \
  -jar career-plan-2026-1.0.0.jar \
  --spring.profiles.active=staging > app.log 2>&1 &
```

#### 步骤 6: 等待启动

```bash
# 等待 25 秒
sleep 25

# 检查进程
ps aux | grep career-plan

# 检查端口
lsof -i :9997
```

#### 步骤 7: 验证服务

```bash
# 1. 健康检查
curl http://localhost:9997/back-server/actuator/health
# 期望：{"status":"UP"}

# 2. 登录测试（所有测试账号）
curl -X POST http://localhost:9997/back-server/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
# 期望：{"code":200,"message":"登录成功",...}

curl -X POST http://localhost:9997/back-server/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
# 期望：{"code":200,"message":"登录成功",...}

curl -X POST http://localhost:9997/back-server/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"advisor1","password":"Advisor1234!"}'
# 期望：{"code":200,"message":"登录成功",...}

# 3. 安全验证（未授权访问）
curl -X PUT http://localhost:9997/back-server/api/v1/user/profile \
  -H "Content-Type: application/json" \
  -d '{"name":"test"}'
# 期望：{"code":401,"message":"未授权访问"}

# 4. 用户资料 API
TOKEN=<从登录响应获取>
curl -X GET http://localhost:9997/back-server/api/v1/user/profile \
  -H "Authorization: Bearer $TOKEN"
# 期望：{"code":200,"message":"查询成功",...}

# 5. 查看日志（确认无 ERROR）
tail -100 /opt/career-plan/staging/app.log | grep -i "error\|exception"
# 期望：无输出或仅有 INFO/WARN
```

### Production 环境部署

**生产环境部署必须在 Staging 验证通过后进行！**

```bash
# 步骤与 Staging 类似，注意以下区别：

# 1. 目录不同
cd /opt/career-plan/production

# 2. 端口不同
lsof -i :9999

# 3. Profile 不同
--spring.profiles.active=production

# 4. Security 必须启用
# application-production.yml 中 spring.security.enabled: true

# 5. 验证使用生产域名
curl https://plan.shujuyunxiang.com/back-server/actuator/health
```

## 回滚流程

### 发现问题立即回滚

```bash
# 1. 停止当前服务
pkill -9 java

# 2. 恢复备份 JAR
cd /opt/career-plan/staging
cp career-plan-2026-1.0.0.jar.backup.YYYYMMDD-HHMMSS career-plan-2026-1.0.0.jar

# 3. 重启服务
nohup java -Xms512m -Xmx2g -jar career-plan-2026-1.0.0.jar \
  --spring.profiles.active=staging > app.log 2>&1 &

# 4. 验证
sleep 25
curl http://localhost:9997/back-server/actuator/health
```

## 监控与告警

### 服务监控

```bash
# 创建监控脚本
cat > /opt/career-plan/staging/monitor.sh << 'EOF'
#!/bin/bash
PORT=9997
LOG_FILE="/opt/career-plan/staging/monitor.log"

while true; do
    if ! lsof -i :$PORT > /dev/null 2>&1; then
        echo "$(date): Service not running on port $PORT, restarting..." >> $LOG_FILE
        pkill -9 java
        sleep 3
        cd /opt/career-plan/staging
        nohup java -Xms512m -Xmx2g -jar career-plan-2026-1.0.0.jar \
            --spring.profiles.active=staging > app.log 2>&1 &
        echo "$(date): Service restarted" >> $LOG_FILE
    fi
    sleep 30
done
EOF

chmod +x /opt/career-plan/staging/monitor.sh
```

### 日志监控

```bash
# 实时查看错误日志
tail -f /opt/career-plan/staging/app.log | grep -i "error\|exception"

# 查看最新 100 行
tail -100 /opt/career-plan/staging/app.log
```

## 常见问题

### Q1: 服务启动失败，端口被占用
**A**: 
```bash
# 查找占用进程
lsof -i :9997

# 杀死进程
kill -9 <PID>

# 重启服务
```

### Q2: 健康检查通过但登录失败
**A**: 
1. 检查数据库连接
2. 检查 JWT Secret 配置
3. 检查测试账号密码

### Q3: 日志不更新
**A**: 
1. 检查日志文件权限
2. 检查应用是否真的在运行
3. 查看进程启动时间

### Q4: 密码哈希不匹配
**A**: 
```bash
# 使用 Python 生成正确的 bcrypt 哈希
python3 -c "import bcrypt; print(bcrypt.hashpw('password'.encode(), bcrypt.gensalt()).decode())"

# 更新数据库
mysql -u root -p -e "UPDATE users SET password = '<哈希>' WHERE username = 'admin';"
```

## 验证清单

### 部署后必须验证

- [ ] 健康检查通过
- [ ] 所有测试账号登录成功
- [ ] 未授权访问返回 401
- [ ] 用户资料 API 正常
- [ ] 密码修改 API 正常
- [ ] 日志无 ERROR
- [ ] 服务进程稳定运行

### 生产环境额外验证

- [ ] HTTPS 访问正常
- [ ] Nginx 反向代理正常
- [ ] Security 已启用
- [ ] 数据库连接正常
- [ ] 备份策略已配置

## 修订历史

| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| 1.0 | 2026-03-19 | career-worker | 初始版本（基于 Phase 2 经验） |
