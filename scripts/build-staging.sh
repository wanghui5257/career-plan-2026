#!/bin/bash
# Staging 环境构建脚本
# 用法：./build-staging.sh

set -e

echo "======================================"
echo "🔨 Staging 环境构建开始"
echo "======================================"

# 进入后端目录
cd "$(dirname "$0")/../backend"

# 清理并构建
echo "📦 执行 Maven 构建..."
mvn clean package -DskipTests

# 验证 JAR 文件
JAR_FILE="target/career-plan-2026-1.0.0.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ 构建失败：JAR 文件不存在"
    exit 1
fi

# 验证 JAR 大小（应该在 75-77MB）
JAR_SIZE=$(ls -lh "$JAR_FILE" | awk '{print $5}')
echo "✅ JAR 文件大小：$JAR_SIZE"

# 输出成功信息
echo "======================================"
echo "✅ Staging 构建完成"
echo "📦 JAR 文件：$JAR_FILE"
echo "📊 文件大小：$JAR_SIZE"
echo "======================================"
