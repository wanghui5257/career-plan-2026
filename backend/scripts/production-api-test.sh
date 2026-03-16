#!/bin/bash
# 生产环境 API 接口测试脚本
# 用途：验证所有 API 接口正常工作

set -e

# 配置
PROD_HOST="47.115.63.159"
PROD_PORT="9999"
CONTEXT_PATH="/back-server"
BASE_URL="http://${PROD_HOST}:${PROD_PORT}${CONTEXT_PATH}"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "  生产环境 API 接口测试"
echo "=========================================="
echo "目标地址：${BASE_URL}"
echo "测试时间：$(date '+%Y-%m-%d %H:%M:%S')"
echo ""

PASS_COUNT=0
FAIL_COUNT=0

# 测试函数
test_api() {
    local method=$1
    local endpoint=$2
    local expected_status=$3
    local description=$4
    local data=${5:-""}
    
    echo -n "测试：${description} ... "
    
    if [ -n "$data" ]; then
        HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X "${method}" \
            "${BASE_URL}${endpoint}" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X "${method}" \
            "${BASE_URL}${endpoint}")
    fi
    
    if [ "$HTTP_STATUS" = "$expected_status" ]; then
        echo -e "${GREEN}✅ PASS${NC} (HTTP ${HTTP_STATUS})"
        ((PASS_COUNT++))
    else
        echo -e "${RED}❌ FAIL${NC} (期望：${expected_status}, 实际：${HTTP_STATUS})"
        ((FAIL_COUNT++))
    fi
}

echo "--- 健康检查接口 ---"
test_api "GET" "/actuator/health" "200" "健康检查"
test_api "GET" "/actuator/info" "200" "应用信息"

echo ""
echo "--- 认证接口 ---"
test_api "POST" "/api/v1/auth/login" "200" "登录 - 正确凭证" '{"username":"admin","password":"admin123"}'
test_api "POST" "/api/v1/auth/login" "401" "登录 - 错误密码" '{"username":"admin","password":"wrong"}'
test_api "POST" "/api/v1/auth/login" "401" "登录 - 用户不存在" '{"username":"nobody","password":"test"}'
test_api "POST" "/api/v1/auth/logout" "401" "登出 - 未授权 (预期)"

echo ""
echo "--- 任务接口 ---"
test_api "GET" "/api/v1/tasks" "200" "获取任务列表"
test_api "GET" "/api/v1/tasks/1" "200" "获取单个任务"

echo ""
echo "--- 进度报告接口 ---"
test_api "GET" "/api/v1/progress-reports" "200" "获取进度报告列表"

echo ""
echo "=========================================="
echo "  测试结果汇总"
echo "=========================================="
echo -e "  通过：${GREEN}${PASS_COUNT}${NC}"
echo -e "  失败：${RED}${FAIL_COUNT}${NC}"
echo "完成时间：$(date '+%Y-%m-%d %H:%M:%S')"

if [ $FAIL_COUNT -gt 0 ]; then
    echo ""
    echo -e "${RED}⚠️  部分测试失败，请检查服务状态${NC}"
    exit 1
else
    echo ""
    echo -e "${GREEN}✅ 所有测试通过！${NC}"
fi
