#!/bin/bash
# 交付物验证脚本
# 用途：验证测试文档和代码交付的完整性

set -e

echo "=========================================="
echo "  职业发展计划 2026 - 交付物验证"
echo "=========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 计数器
PASS=0
FAIL=0
WARN=0

# 验证函数
check_file() {
    local file=$1
    local desc=$2
    
    if [ -f "$file" ]; then
        echo -e "${GREEN}✓${NC} $desc"
        ((PASS++))
    else
        echo -e "${RED}✗${NC} $desc (文件不存在：$file)"
        ((FAIL++))
    fi
}

check_content() {
    local file=$1
    local pattern=$2
    local desc=$3
    
    if grep -q "$pattern" "$file" 2>/dev/null; then
        echo -e "${GREEN}✓${NC} $desc"
        ((PASS++))
    else
        echo -e "${RED}✗${NC} $desc (内容不匹配)"
        ((FAIL++))
    fi
}

check_api() {
    local url=$1
    local desc=$2
    
    response=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 "$url" 2>/dev/null || echo "000")
    
    if [ "$response" = "200" ] || [ "$response" = "201" ]; then
        echo -e "${GREEN}✓${NC} $desc (HTTP $response)"
        ((PASS++))
    elif [ "$response" = "000" ]; then
        echo -e "${YELLOW}!${NC} $desc (无法连接)"
        ((WARN++))
    else
        echo -e "${RED}✗${NC} $desc (HTTP $response)"
        ((FAIL++))
    fi
}

# 基础路径
BASE_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
TESTS_DIR="$BASE_DIR/tests"

echo "📁 验证测试文档结构..."
echo "----------------------------------------"

# 检查目录结构
check_file "$TESTS_DIR/plans/TEST-PLAN-001.md" "测试计划文档"
check_file "$TESTS_DIR/cases/TEST-CASE-AUTH.md" "认证测试用例"
check_file "$TESTS_DIR/cases/TEST-CASE-TASKS.md" "任务管理测试用例"
check_file "$TESTS_DIR/cases/TEST-CASE-FRONTEND.md" "前端测试用例"
check_file "$TESTS_DIR/reports/TEST-REPORT-TEMPLATE.md" "测试报告模板"
check_file "$TESTS_DIR/scripts/verify-delivery.sh" "验证脚本"

echo ""
echo "📋 验证测试计划内容..."
echo "----------------------------------------"

if [ -f "$TESTS_DIR/plans/TEST-PLAN-001.md" ]; then
    check_content "$TESTS_DIR/plans/TEST-PLAN-001.md" "测试目标" "包含测试目标"
    check_content "$TESTS_DIR/plans/TEST-PLAN-001.md" "测试范围" "包含测试范围"
    check_content "$TESTS_DIR/plans/TEST-PLAN-001.md" "测试进度" "包含测试进度"
    check_content "$TESTS_DIR/plans/TEST-PLAN-001.md" "风险评估" "包含风险评估"
fi

echo ""
echo "📝 验证测试用例内容..."
echo "----------------------------------------"

for case_file in "$TESTS_DIR/cases/"*.md; do
    if [ -f "$case_file" ]; then
        filename=$(basename "$case_file")
        check_content "$case_file" "用例 ID" "[$filename] 包含用例 ID"
        check_content "$case_file" "测试步骤" "[$filename] 包含测试步骤"
        check_content "$case_file" "预期结果" "[$filename] 包含预期结果"
    fi
done

echo ""
echo "🌐 验证 API 可用性..."
echo "----------------------------------------"

API_BASE="https://plan.shujuyunxiang.com/api/v1"

check_api "$API_BASE/../actuator/health" "健康检查接口"
check_api "$API_BASE/auth/login" "登录接口 (POST)"

echo ""
echo "=========================================="
echo "  验证结果汇总"
echo "=========================================="
echo -e "${GREEN}通过${NC}: $PASS"
echo -e "${RED}失败${NC}: $FAIL"
echo -e "${YELLOW}警告${NC}: $WARN"
echo ""

if [ $FAIL -eq 0 ]; then
    echo -e "${GREEN}✅ 交付物验证通过！${NC}"
    exit 0
else
    echo -e "${RED}❌ 交付物验证失败！${NC}"
    exit 1
fi
