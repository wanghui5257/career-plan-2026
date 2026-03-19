#!/bin/bash
# Staging 环境部署脚本
# 用法：./deploy-staging.sh

set -e

SERVER="47.115.63.159"
SSH_KEY="$HOME/.ssh/id_ed25519"
DEPLOY_DIR="/opt/career-plan/staging"
JAR_FILE="../backend/target/career-plan-2026-1.0.0.jar"
PORT=9997

echo "======================================"
echo "🚀 Staging 环境部署开始"
echo "======================================"

# 验证 JAR 文件存在
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR 文件不存在：$JAR_FILE"
    echo "💡 请先运行：./build-staging.sh"
    exit 1
fi

echo "📦 JAR 文件：$JAR_FILE"

# SSH 连接测试
echo "🔐 测试 SSH 连接..."
if ! ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no -o ConnectTimeout=5 root@$SERVER "echo 'SSH 连接成功'"; then
    echo "❌ SSH 连接失败"
    exit 1
fi

# 停止旧服务
echo "🛑 停止旧服务..."
ssh -i "$SSH_KEY" root@$SERVER "pkill -9 java || true"
sleep 3

# 验证端口已释放
echo "🔍 验证端口 $PORT 已释放..."
if ssh -i "$SSH_KEY" root@$SERVER "lsof -i :$PORT" 2>/dev/null; then
    echo "❌ 端口 $PORT 仍被占用"
    exit 1
fi
echo "✅ 端口 $PORT 已释放"

# 上传 JAR 文件
echo "📤 上传 JAR 文件..."
scp -i "$SSH_KEY" "$JAR_FILE" root@$SERVER:$DEPLOY_DIR/

# 启动新服务
echo "🔥 启动新服务..."
ssh -i "$SSH_KEY" root@$SERVER << 'EOF'
cd /opt/career-plan/staging
nohup java -Xms512m -Xmx2g \
  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/opt/career-plan/staging/heapdump.hprof \
  -jar career-plan-2026-1.0.0.jar \
  --spring.profiles.active=staging > app.log 2>&1 &
EOF

# 等待启动
echo "⏳ 等待服务启动（25 秒）..."
sleep 25

# 健康检查
echo "🏥 健康检查..."
HEALTH=$(ssh -i "$SSH_KEY" root@$SERVER "curl -s http://localhost:$PORT/back-server/actuator/health")
if echo "$HEALTH" | grep -q '"status":"UP"'; then
    echo "✅ 健康检查通过：$HEALTH"
else
    echo "❌ 健康检查失败：$HEALTH"
    exit 1
fi

# 登录测试
echo "🔐 登录测试..."
LOGIN=$(ssh -i "$SSH_KEY" root@$SERVER "curl -s -X POST http://localhost:$PORT/back-server/api/v1/auth/login -H 'Content-Type: application/json' -d '{\"username\":\"admin\",\"password\":\"123456\"}'")
if echo "$LOGIN" | grep -q '"code":200'; then
    echo "✅ 登录测试通过"
else
    echo "❌ 登录测试失败：$LOGIN"
    exit 1
fi

echo "======================================"
echo "✅ Staging 部署完成"
echo "🌐 访问地址：https://staging.plan.shujuyunxiang.com/back-server"
echo "======================================"
