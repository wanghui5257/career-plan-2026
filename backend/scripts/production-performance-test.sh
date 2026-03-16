#!/bin/bash
# 生产环境性能测试脚本
# 用途：测试 API 响应时间和并发性能

set -e

# 配置
PROD_HOST="47.115.63.159"
PROD_PORT="9999"
CONTEXT_PATH="/back-server"
BASE_URL="http://${PROD_HOST}:${PROD_PORT}${CONTEXT_PATH}"

# 颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo "=========================================="
echo "  生产环境性能测试"
echo "=========================================="
echo "目标地址：${BASE_URL}"
echo "测试时间：$(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# 性能测试函数
test_endpoint_performance() {
    local endpoint=$1
    local description=$2
    local method=${3:-GET}
    local data=${4:-""}
    
    echo "测试：${description}"
    echo "端点：${method} ${endpoint}"
    
    # 进行 5 次请求，计算平均响应时间
    TOTAL_TIME=0
    COUNT=5
    
    for i in $(seq 1 $COUNT); do
        if [ -n "$data" ]; then
            TIME_MS=$(curl -s -o /dev/null -w "%{time_total}" -X "${method}" \
                "${BASE_URL}${endpoint}" \
                -H "Content-Type: application/json" \
                -d "$data")
        else
            TIME_MS=$(curl -s -o /dev/null -w "%{time_total}" -X "${method}" \
                "${BASE_URL}${endpoint}")
        fi
        
        # 转换为毫秒
        TIME_MS_INT=$(echo "$TIME_MS * 1000" | bc | cut -d'.' -f1)
        TOTAL_TIME=$((TOTAL_TIME + TIME_MS_INT))
        echo "  请求 ${i}: ${TIME_MS_INT}ms"
    done
    
    AVG_TIME=$((TOTAL_TIME / COUNT))
    echo ""
    echo "  平均响应时间：${AVG_TIME}ms"
    
    # 性能评估
    if [ $AVG_TIME -lt 100 ]; then
        echo -e "  性能评级：${GREEN}优秀 (<100ms)${NC}"
    elif [ $AVG_TIME -lt 300 ]; then
        echo -e "  性能评级：${GREEN}良好 (<300ms)${NC}"
    elif [ $AVG_TIME -lt 500 ]; then
        echo -e "  性能评级：${YELLOW}一般 (<500ms)${NC}"
    else
        echo -e "  性能评级：${RED}需要优化 (>=500ms)${NC}"
    fi
    echo ""
}

echo "--- 健康检查接口性能 ---"
test_endpoint_performance "/actuator/health" "健康检查"

echo "--- 认证接口性能 ---"
test_endpoint_performance "/api/v1/auth/login" "登录接口" "POST" '{"username":"admin","password":"admin123"}'

echo "--- 任务接口性能 ---"
test_endpoint_performance "/api/v1/tasks" "获取任务列表"
test_endpoint_performance "/api/v1/tasks/1" "获取单个任务"

echo "--- 进度报告接口性能 ---"
test_endpoint_performance "/api/v1/progress-reports" "获取进度报告列表"

echo "=========================================="
echo "  性能测试完成"
echo "=========================================="
echo "完成时间：$(date '+%Y-%m-%d %H:%M:%S')"
echo ""
echo "💡 建议："
echo "  - 平均响应时间应 < 300ms"
echo "  - 如超过 500ms，考虑优化数据库查询或添加缓存"
