# ECS 服务器登录验证脚本

# 在 ECS 服务器上执行此脚本，自动验证登录功能

echo "=========================================="
echo "  Career Plan 2026 - 自动验证脚本"
echo "=========================================="

# 1. 检查应用
echo ""
echo "1️⃣ 检查应用状态..."
if pgrep -f "career-plan" > /dev/null; then
    echo "✅ 应用正在运行"
else
    echo "❌ 应用未运行"
    exit 1
fi

# 2. 检查端口
echo ""
echo "2️⃣ 检查端口..."
if netstat -tlnp | grep -q 9999; then
    echo "✅ 端口 9999 正在监听"
else
    echo "❌ 端口 9999 未监听"
    exit 1
fi

# 3. 测试健康检查
echo ""
echo "3️⃣ 健康检查..."
curl -s -m 5 "http://[::1]:9999/actuator/health"

# 4. 生成新的 BCrypt 哈希
echo ""
echo "4️⃣ 生成新的 BCrypt 哈希..."
HASH_RESPONSE=$(curl -s -m 5 "http://[::1]:9999/api/v1/test/hash?password=admin123")
echo "$HASH_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$HASH_RESPONSE"

# 提取哈希值
NEW_HASH=$(echo "$HASH_RESPONSE" | grep -o '"hash":"[^"]*"' | cut -d'"' -f4)
if [ -n "$NEW_HASH" ]; then
    echo ""
    echo "✅ 新哈希值：$NEW_HASH"
    echo ""
    echo "📝 请执行以下 SQL 更新密码："
    echo "mysql -u root -p"
    echo "USE career_plan;"
    echo "UPDATE users SET password = '$NEW_HASH' WHERE username = 'admin';"
    echo "EXIT;"
else
    echo "❌ 无法生成哈希值，请检查应用日志"
fi

# 5. 测试当前密码验证
echo ""
echo "5️⃣ 测试密码验证接口..."
VERIFY_RESPONSE=$(curl -s -m 5 "http://[::1]:9999/api/v1/test/verify?password=admin123&hash=\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
echo "$VERIFY_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$VERIFY_RESPONSE"

echo ""
echo "=========================================="
echo "  验证完成"
echo "=========================================="
