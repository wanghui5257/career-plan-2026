# 版本发布流程

## 适用范围
career-plan-2026 SaaS 平台生产环境发布

---

## 发布前准备

### 1. 代码准备
- [ ] 所有功能代码已合并到目标分支
- [ ] 本地编译测试通过
- [ ] Git 提交记录清晰

### 2. 验证准备
- [ ] 准备验证报告模板
- [ ] 确认测试账号可用（admin / 123456）
- [ ] 确认 MinIO 同步正常

---

## 发布步骤

### 步骤 1：Maven 编译打包
```bash
cd /root/tasks/career-plan-2026/backend
mvn clean package -DskipTests -Dmaven.test.skip=true
```
**验证**：`ls -lh target/career-plan-2026-1.0.0.jar`

### 步骤 2：部署到生产服务器
```bash
# 方式 A：scp 直接上传
scp -i <ssh_key> target/career-plan-2026-1.0.0.jar root@47.115.63.159:/opt/career-plan/backend/target/

# 方式 B：通过 MinIO 中转
mc cp target/career-plan-2026-1.0.0.jar hiclaw/hiclaw-storage/shared/tasks/.../artifacts/
```

### 步骤 3：服务重启
```bash
# 生产服务器执行
cd /opt/career-plan/backend/target
pkill -f 'career-plan-2026-1.0.0.jar' || true
sleep 3
nohup java -Xms1G -Xmx2G -jar career-plan-2026-1.0.0.jar > /opt/career-plan/backend/logs/app.log 2>&1 &
```

---

## 发布验证（必须执行）

### career-worker 执行（8 项检查）

| 步骤 | 验证项 | 命令 | 预期结果 | 状态 |
|------|--------|------|----------|------|
| 1 | 进程检查 | `ps aux \| grep career-plan` | 进程存在 | ⬜ |
| 2 | 端口检查 | `netstat -tlnp \| grep 9999` | 端口监听 | ⬜ |
| 3 | 健康检查 | `curl /actuator/health` | `{"status":"UP"}` | ⬜ |
| 4 | 登录 API | `POST /api/v1/auth/login` | 200 + Token | ⬜ |
| 5 | Tasks API | `GET /api/v1/tasks` | 200 + 数据 | ⬜ |
| 6 | Plans API | `GET /api/v1/plans` | 200 | ⬜ |
| 7 | Progress API | `GET /api/v1/progress/summary` | 200 | ⬜ |
| 8 | 前端部署 | `ls /www/wwwroot/.../assets/` | JS 文件存在 | ⬜ |

**验证脚本**：
```bash
# 健康检查
curl -s https://plan.shujuyunxiang.com/back-server/actuator/health

# 获取 Token
TOKEN=$(curl -s -X POST https://plan.shujuyunxiang.com/back-server/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 测试所有 API
echo "=== Tasks API ==="
curl -s -w "\nHTTP: %{http_code}\n" https://plan.shujuyunxiang.com/back-server/api/v1/tasks \
  -H "Authorization: Bearer $TOKEN" | head -5

echo "=== Plans API ==="
curl -s -w "\nHTTP: %{http_code}\n" https://plan.shujuyunxiang.com/back-server/api/v1/plans \
  -H "Authorization: Bearer $TOKEN"

echo "=== Progress API ==="
curl -s -w "\nHTTP: %{http_code}\n" https://plan.shujuyunxiang.com/back-server/api/v1/progress/summary \
  -H "Authorization: Bearer $TOKEN"
```

---

## QA 独立验证

### qa-tester 执行

1. **执行 API 验证**（使用上述验证脚本）
2. **生成验证报告**（`API-VERIFICATION-REPORT.md`）
3. **同步到 MinIO 归档**

**验证报告模板**：
```markdown
# API 验证报告

## 验证时间
YYYY-MM-DD HH:MM

## 验证结果
| API | 状态码 | 结果 |
|-----|--------|------|
| /actuator/health | - | {"status":"UP"} |
| /api/v1/auth/login | 200 | Token 获取成功 |
| /api/v1/tasks | 200 | 返回任务数据 |
| /api/v1/plans | 200 | 返回数据 |
| /api/v1/progress/summary | 200 | 验证通过 |

## 结论
✅ 所有 API 验证通过，系统功能正常
```

**同步命令**：
```bash
mc cp API-VERIFICATION-REPORT.md \
   hiclaw/hiclaw-storage/shared/tasks/task-20260317-083500/artifacts/
```

---

## admin 最终验收

### 前端功能测试

**访问地址**：https://plan.shujuyunxiang.com  
**测试账号**：admin / 123456  

**⚠️ 必须强制刷新浏览器**：
- Windows: `Ctrl + F5`
- Mac: `Cmd + Shift + R`

| 测试项 | 预期结果 | 状态 |
|--------|----------|------|
| 1. 首页加载 | 页面正常显示 | ⬜ |
| 2. 登录 | admin/123456 成功 | ⬜ |
| 3. 任务列表 | 显示任务数据 | ⬜ |
| 4. 创建计划按钮 | 点击弹出模态框 | ⬜ |
| 5. 创建任务按钮 | 点击弹出模态框 | ⬜ |
| 6. 浏览器控制台 | 无 403 错误 | ⬜ |

---

## 发布完成确认

### 所有检查完成后

- [ ] career-worker 完成 8 项发布验证
- [ ] qa-tester 完成独立 API 验证
- [ ] 验证报告已同步到 MinIO
- [ ] admin 完成前端验收
- [ ] 所有问题已修复

**发布状态**：✅ 成功 / ❌ 失败（回滚）

---

## 回滚流程（如发布失败）

```bash
# 1. 停止服务
pkill -f 'career-plan-2026-1.0.0.jar'

# 2. 恢复旧版本 JAR
cp /opt/career-plan/backend/target/career-plan-2026-1.0.0.jar.backup \
   /opt/career-plan/backend/target/career-plan-2026-1.0.0.jar

# 3. 重启服务
nohup java -jar career-plan-2026-1.0.0.jar > /opt/career-plan/backend/logs/app.log 2>&1 &

# 4. 验证回滚
curl -s https://plan.shujuyunxiang.com/back-server/actuator/health
```

---

## 常见问题排查

### 服务启动失败
```bash
# 查看日志
tail -200 /opt/career-plan/backend/logs/app.log

# 检查端口占用
netstat -tlnp | grep 9999

# 杀死旧进程
pkill -9 -f 'career-plan'
```

### API 返回 403
- 检查 JWT Token 是否有效
- 检查 SecurityConfig 配置
- 检查实体类字段映射（@Column 注解）

### 前端不显示数据
- 强制刷新浏览器（Ctrl+F5）
- 检查浏览器控制台错误
- 验证 API 是否正常返回

---

## 文档维护

- **维护者**：saas-architect
- **最后更新**：2026-03-18
- **版本**：v1.0
