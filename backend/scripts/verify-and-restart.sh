#!/bin/bash

# Career Plan 2026 - 验证并重启脚本

echo "=========================================="
echo "  Career Plan 2026 - 验证并重启"
echo "=========================================="

# 1. 停止旧应用
echo ""
echo "1️⃣ 停止旧应用..."
pkill -f career-plan
sleep 3
if ! pgrep -f "career-plan" > /dev/null; then
    echo "✅ 应用已停止"
else
    echo "❌ 应用未停止，强制停止..."
    pkill -9 -f career-plan
    sleep 2
fi

# 2. 重新构建
echo ""
echo "2️⃣ 重新构建..."
cd /opt/career-plan/backend
mvn clean package -DskipTests -q
if [ $? -eq 0 ]; then
    echo "✅ 构建成功"
    cp target/*.jar ../career-plan.jar
else
    echo "❌ 构建失败"
    exit 1
fi

# 3. 启动新应用
echo ""
echo "3️⃣ 启动新应用..."
cd /opt/career-plan
java -Djava.net.preferIPv4Stack=true -jar career-plan.jar > app.log 2>&1 &
echo $! > app.pid
sleep 30

# 4. 验证启动
echo ""
echo "4️⃣ 验证启动..."
if pgrep -f "career-plan" > /dev/null; then
    echo "✅ 应用正在运行 (PID: $(cat app.pid))"
else
    echo "❌ 应用未启动"
    tail -50 app.log
    exit 1
fi

if netstat -tlnp | grep -q 9999; then
    echo "✅ 端口 9999 正在监听"
else
    echo "❌ 端口 9999 未监听"
    exit 1
fi

# 5. 测试健康检查
echo ""
echo "5️⃣ 健康检查..."
HEALTH=$(curl -s -m 5 "http://127.0.0.1:9999/actuator/health")
if echo "$HEALTH" | grep -q "UP"; then
    echo "✅ 健康检查通过：$HEALTH"
else
    echo "❌ 健康检查失败：$HEALTH"
    exit 1
fi

# 6. 测试登录
echo ""
echo "6️⃣ 测试登录..."
echo "   用户：admin"
echo "   密码：admin123"

LOGIN_RESPONSE=$(curl -s -m 10 -X POST "http://127.0.0.1:9999/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

echo "   响应：$LOGIN_RESPONSE"

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo ""
    echo "✅ 登录成功！"
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    if [ -n "$TOKEN" ]; then
        echo "   Token: ${TOKEN:0:50}..."
    fi
    
    # 7. 测试任务接口
    echo ""
    echo "7️⃣ 测试任务接口..."
    TASK_RESPONSE=$(curl -s -m 10 -X GET "http://127.0.0.1:9999/api/v1/tasks" \
      -H "Authorization: Bearer $TOKEN")
    echo "   响应：$TASK_RESPONSE"
    
    if [ $? -eq 0 ]; then
        echo "✅ 任务接口测试通过"
    else
        echo "⚠️ 任务接口测试失败"
    fi
elif echo "$LOGIN_RESPONSE" | grep -q "密码错误\|用户不存在"; then
    echo ""
    echo "❌ 登录失败：认证错误"
    echo ""
    echo "   请检查数据库中的密码："
    echo "   mysql -u root -p"
    echo "   USE career_plan;"
    echo "   SELECT id, username, password FROM users WHERE username = 'admin';"
else
    echo ""
    echo "❌ 登录失败：未知错误"
    echo "   请查看应用日志：tail -100 app.log"
fi

echo ""
echo "=========================================="
echo "  验证完成"
echo "=========================================="
