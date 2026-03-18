#!/bin/bash
# 生产环境数据库连接检查脚本
# 用途：验证数据库连接和数据完整性

set -e

# 配置
DB_HOST="47.115.63.159"
DB_PORT="3306"
DB_NAME="career_plan"
DB_USER="career_user"
# 注意：生产环境密码应从安全位置获取，此处为示例
DB_PASS="career_pass_2026"

echo "=========================================="
echo "  生产环境数据库检查"
echo "=========================================="
echo "数据库地址：${DB_HOST}:${DB_PORT}"
echo "数据库名称：${DB_NAME}"
echo "检查时间：$(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# 1. 检查数据库连接
echo "[1/4] 检查数据库连接..."
if mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" -e "SELECT 1;" > /dev/null 2>&1; then
    echo "  ✅ 数据库连接成功"
else
    echo "  ❌ 数据库连接失败"
    exit 1
fi

# 2. 检查表结构
echo "[2/4] 检查表结构..."
TABLES=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" -N -e "SHOW TABLES;")
EXPECTED_TABLES="users tasks progress_reports"

for table in $EXPECTED_TABLES; do
    if echo "$TABLES" | grep -q "$table"; then
        echo "  ✅ 表 ${table} 存在"
    else
        echo "  ❌ 表 ${table} 不存在"
        exit 1
    fi
done

# 3. 检查数据完整性
echo "[3/4] 检查数据完整性..."

# 检查用户表
USER_COUNT=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" -N -e "SELECT COUNT(*) FROM users;")
echo "  用户数量：${USER_COUNT}"
if [ "$USER_COUNT" -gt 0 ]; then
    echo "  ✅ 用户表有数据"
else
    echo "  ⚠️  用户表为空"
fi

# 检查任务表
TASK_COUNT=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" -N -e "SELECT COUNT(*) FROM tasks;")
echo "  任务数量：${TASK_COUNT}"
if [ "$TASK_COUNT" -gt 0 ]; then
    echo "  ✅ 任务表有数据"
else
    echo "  ⚠️  任务表为空"
fi

# 检查进度报告表
REPORT_COUNT=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" -N -e "SELECT COUNT(*) FROM progress_reports;")
echo "  进度报告数量：${REPORT_COUNT}"

# 4. 检查索引
echo "[4/4] 检查索引..."
INDEXES=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" -N -e "SHOW INDEX FROM users;" | wc -l)
echo "  用户表索引数：${INDEXES}"

echo ""
echo "=========================================="
echo "  ✅ 数据库检查完成！"
echo "=========================================="
echo "完成时间：$(date '+%Y-%m-%d %H:%M:%S')"
