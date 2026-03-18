#!/bin/bash
# 生产环境健康检查脚本
# 用途：验证生产环境服务是否正常运行

set -e

# 配置
PROD_HOST="47.115.63.159"
PROD_PORT="9999"
CONTEXT_PATH="/back-server"
BASE_URL="http://${PROD_HOST}:${PROD_PORT}${CONTEXT_PATH}"

echo "=========================================="
echo "  生产环境健康检查"
echo "=========================================="
echo "目标地址：${BASE_URL}"
echo "检查时间：$(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# 1. 检查服务是否可访问
echo "[1/4] 检查服务连通性..."
if curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/actuator/health" | grep -q "200"; then
    echo "  ✅ 服务可访问"
else
    echo "  ❌ 服务不可访问"
    exit 1
fi

# 2. 检查健康状态
echo "[2/4] 检查健康状态..."
HEALTH_RESPONSE=$(curl -s "${BASE_URL}/actuator/health")
if echo "$HEALTH_RESPONSE" | grep -q '"status":"UP"'; then
    echo "  ✅ 健康状态：UP"
    echo "  详细状态：$HEALTH_RESPONSE"
else
    echo "  ❌ 健康状态异常"
    echo "  响应：$HEALTH_RESPONSE"
    exit 1
fi

# 3. 检查 API 文档可访问性
echo "[3/4] 检查 API 文档..."
if curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/swagger-ui.html" | grep -q "200"; then
    echo "  ✅ Swagger UI 可访问"
else
    echo "  ❌ Swagger UI 不可访问"
    exit 1
fi

# 4. 检查 API 接口
echo "[4/4] 检查 API 接口..."
if curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/api/v1/auth/login" -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"test"}' | grep -q "401"; then
    echo "  ✅ API 接口正常响应 (401 未授权是预期行为)"
else
    echo "  ❌ API 接口异常"
    exit 1
fi

echo ""
echo "=========================================="
echo "  ✅ 所有健康检查通过！"
echo "=========================================="
echo "完成时间：$(date '+%Y-%m-%d %H:%M:%S')"
