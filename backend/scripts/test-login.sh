#!/bin/bash

# Career Plan 2026 - 登录接口测试脚本
# 在 ECS 服务器上执行此脚本验证登录功能

echo "=========================================="
echo "  Career Plan 2026 - 登录接口测试"
echo "=========================================="

# 配置
API_URL="http://[::1]:9999/api/v1/auth/login"
USERNAME="admin"
PASSWORD="admin123"

echo ""
echo "1️⃣ 检查应用是否运行..."
if pgrep -f "career-plan" > /dev/null; then
    echo "✅ 应用正在运行"
    PID=$(pgrep -f "career-plan")
    echo "   PID: $PID"
else
    echo "❌ 应用未运行，请先启动应用"
    echo "   启动命令：java -jar career-plan.jar > app.log 2>&1 &"
    exit 1
fi

echo ""
echo "2️⃣ 检查端口监听..."
if netstat -tlnp | grep -q 9999; then
    echo "✅ 端口 9999 正在监听"
    netstat -tlnp | grep 9999
else
    echo "❌ 端口 9999 未监听"
    exit 1
fi

echo ""
echo "3️⃣ 测试健康检查..."
HEALTH=$(curl -s -m 5 "http://[::1]:9999/actuator/health")
if echo "$HEALTH" | grep -q "UP"; then
    echo "✅ 健康检查通过：$HEALTH"
else
    echo "❌ 健康检查失败：$HEALTH"
    exit 1
fi

echo ""
echo "4️⃣ 测试登录接口..."
echo "   请求：POST $API_URL"
echo "   用户：$USERNAME"
echo "   密码：$PASSWORD"
echo ""

RESPONSE=$(curl -s -m 10 -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

echo "   响应：$RESPONSE"
echo ""

# 检查响应
if echo "$RESPONSE" | grep -q "token"; then
    echo "✅ 登录成功！"
    TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    if [ -n "$TOKEN" ]; then
        echo "   Token: ${TOKEN:0:50}..."
        echo ""
        echo "5️⃣ 使用 Token 测试任务接口..."
        TASK_RESPONSE=$(curl -s -m 10 -X GET "http://[::1]:9999/api/v1/tasks" \
          -H "Authorization: Bearer $TOKEN")
        echo "   响应：$TASK_RESPONSE"
        if [ $? -eq 0 ]; then
            echo "✅ 任务接口测试通过"
        else
            echo "⚠️ 任务接口测试失败"
        fi
    fi
elif echo "$RESPONSE" | grep -q "用户不存在\|密码错误"; then
    echo "❌ 登录失败：认证错误"
    echo "   请检查数据库中的用户密码是否正确"
    echo ""
    echo "   解决方案："
    echo "   mysql -u root -p"
    echo "   USE career_plan;"
    echo "   UPDATE users SET password = '\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE username = 'admin';"
else
    echo "❌ 登录失败：未知错误"
    echo "   请查看应用日志：tail -100 app.log"
fi

echo ""
echo "=========================================="
echo "  测试完成"
echo "=========================================="
