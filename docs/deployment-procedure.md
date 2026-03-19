# 部署流程 (Deployment Procedure)

## 目的
确保部署过程可重复、可验证、安全。

## 适用范围
- Staging 环境部署
- Production 环境部署

---

## 部署前检查清单

### 1. 代码检查
- [ ] 所有代码已提交到 Git
- [ ] PR 已创建并审查通过
- [ ] 单元测试覆盖率 ≥ 80%
- [ ] P0/P1 问题已 100% 修复

### 2. 测试验证
- [ ] API 测试通过率 ≥ 90%
- [ ] 前端联调通过率 ≥ 90%
- [ ] P0 安全修复已验证（4/4 未授权访问 → 401）

### 3. 文档检查
- [ ] API 文档已更新
- [ ] 部署文档已更新
- [ ] 变更日志已编写

---

## Staging 环境部署

### 前端部署

```bash
# 1. 构建前端
cd /root/hiclaw-fs/shared/tasks/career-plan-2026/
npm run build

# 2. 验证构建产物
ls -lh dist/  # 应约 13.32 MiB

# 3. 同步到 MinIO
mc mirror --overwrite dist/ \
  hiclaw/hiclaw-storage/shared/tasks/career-plan-2026/staging-dist/

# 4. 部署到 Staging
mc mirror --overwrite \
  hiclaw/hiclaw-storage/shared/tasks/career-plan-2026/staging-dist/ \
  /www/wwwroot/staging.plan.shujuyunxiang.com/

# 5. 验证部署
curl -I https://staging.plan.shujuyunxiang.com/
# 应返回 200
```

### 后端部署

```bash
# 1. 构建后端
cd /opt/career-plan/backend/
mvn clean package -DskipTests

# 2. 验证 JAR 包
ls -lh target/career-plan-2026-1.0.0.jar
# 新版本应约 75-77MB（旧版本 59MB）

# 3. 复制 JAR 到 Staging
cp target/career-plan-2026-1.0.0.jar /opt/career-plan/staging/

# 4. 验证目标位置
ls -lh /opt/career-plan/staging/*.jar
# 确认文件大小匹配

# 5. 停止旧服务
pkill -9 java
sleep 3

# 6. 验证端口释放
netstat -tlnp | grep 9997
# 应无输出

# 7. 启动新服务
cd /opt/career-plan/staging/
nohup java -Xms512m -Xmx2g \
  -jar career-plan-2026-1.0.0.jar \
  --spring.profiles.active=staging \
  > app.log 2>&1 &

# 8. 等待启动
sleep 25

# 9. 验证服务
curl http://localhost:9997/back-server/actuator/health
# 应返回 {"status":"UP"}
```

---

## Production 环境部署

### 前置条件
- [ ] Staging 环境验证通过
- [ ] Admin 批准生产部署
- [ ] 备份数据库

### 部署步骤

```bash
# 1. 数据库备份
mysqldump -u root -p career_plan > /backup/career_plan_$(date +%Y%m%d_%H%M%S).sql

# 2. 部署后端（同 Staging）
# ...（参考上方后端部署步骤）

# 3. 部署前端
mc mirror --overwrite \
  hiclaw/hiclaw-storage/shared/tasks/career-plan-2026/dist/ \
  /www/wwwroot/plan.shujuyunxiang.com/

# 4. 验证服务
curl https://plan.shujuyunxiang.com/back-server/actuator/health
# 应返回 {"status":"UP"}
```

---

## 部署验证（关键！）

### 1. 健康检查
```bash
curl https://staging.plan.shujuyunxiang.com/back-server/actuator/health
# 期望：{"status":"UP"}
```

### 2. 登录 API
```bash
curl -X POST https://staging.plan.shujuyunxiang.com/back-server/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
# 期望：{"code":200,"token":"..."}
```

### 3. P0 安全验证
```bash
# 未授权访问应返回 401
curl -X PUT https://staging.plan.shujuyunxiang.com/back-server/api/v1/user/profile \
  -H "Content-Type: application/json" \
  -d '{"name":"test"}'
# 期望：{"code":401,"message":"未授权访问"}
```

### 4. 验证构建产物包含修复
```bash
# 前端：验证修复代码已构建
grep -o "invalid planId" /www/wwwroot/staging.plan.shujuyunxiang.com/assets/*.js
# 应找到修复代码

# 后端：验证 JAR 版本
ls -lh /opt/career-plan/staging/career-plan-2026-1.0.0.jar
# 应约 75-77MB（新版本）
```

### 5. 验证服务日志
```bash
# 查看最新日志
tail -100 /opt/career-plan/staging/app.log | grep -i "started\|up"
# 应看到服务正常启动

# 验证新代码运行
grep "=== TOKEN GENERATION ===" /opt/career-plan/staging/app.log
# 应看到调试日志（如果添加了）
```

---

## 回滚流程

```bash
# 1. 停止服务
pkill -9 java

# 2. 恢复旧版本
cp /backup/career-plan-2026-previous.jar /opt/career-plan/staging/

# 3. 启动旧版本
cd /opt/career-plan/staging/
nohup java -Xms512m -Xmx2g \
  -jar career-plan-2026-previous.jar \
  --spring.profiles.active=staging \
  > app.log 2>&1 &

# 4. 验证回滚
curl http://localhost:9997/back-server/actuator/health
```

---

## 关键注意事项

### ⚠️ 必须遵守的规则

1. **使用域名访问，禁止 IP+端口**
   - ✅ `https://staging.plan.shujuyunxiang.com/back-server`
   - ❌ `http://47.115.63.159:9997/back-server`

2. **验证构建产物再部署**
   - 前端：验证 `dist/` 包含修复代码
   - 后端：验证 JAR 文件大小（75-77MB = 新，59MB = 旧）

3. **部署后必须验证**
   - 健康检查
   - 登录 API
   - P0 安全验证
   - 关键业务功能

4. **浏览器缓存问题**
   - 测试前清除浏览器缓存
   - Chrome: `Ctrl+Shift+Delete` → 清除缓存
   - 或：F12 → Network → "Disable cache" → `Ctrl+F5`

5. **服务重启必须彻底**
   - `pkill -9 java` 杀死所有 Java 进程
   - 验证端口释放：`netstat -tlnp | grep 9997`
   - 等待 3 秒再启动

---

## 常见问题

### Q1: 部署后功能未更新
**原因**：构建产物未更新或浏览器缓存
**解决**：
1. 重新构建：`npm run build` 或 `mvn clean package`
2. 验证构建产物包含修复代码
3. 清除浏览器缓存

### Q2: 服务启动失败
**原因**：端口被占用或配置错误
**解决**：
1. `pkill -9 java` 杀死所有 Java 进程
2. `netstat -tlnp | grep 9997` 验证端口释放
3. 检查配置文件
4. 查看日志：`tail -100 app.log`

### Q3: 登录返回 400/500
**原因**：密码哈希不匹配或服务异常
**解决**：
1. 验证数据库密码哈希
2. 检查服务日志
3. 重新生成 bcrypt 哈希

---

## 修订历史
| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| 1.0 | 2026-03-19 | career-plan-team | Phase 2 初始版本 |
