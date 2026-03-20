#!/bin/bash
#
# Career Plan 2026 - Staging 环境数据初始化脚本
# 用途：Staging 环境数据初始化（替代 DataInitializer）
#

set -e

# 数据库配置
DB_HOST="47.115.63.159"
DB_PORT="3306"
DB_NAME="career_plan_staging"
DB_USER="career_plan_staging"
DB_PASS="xASCetc8LSc24NjA"

# BCrypt 哈希（admin/123456，强度 10）
BCRYPT_HASH='$2b$10$Ry3TZ583DrDOCg90VCSIL.eIw9S4BtMcm17WCIA0k0IwJOqdavR1m'

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查数据库连接
check_db_connection() {
    log_info "检查数据库连接..."
    if ! mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS -e "SELECT 1" --skip-ssl > /dev/null 2>&1; then
        log_error "数据库连接失败"
        exit 1
    fi
    log_info "✅ 数据库连接成功"
}

# 初始化 admin 用户
init_admin_user() {
    log_info "检查 admin 用户..."
    
    # 检查 admin 用户是否存在
    ADMIN_EXISTS=$(mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME \
        -e "SELECT COUNT(*) FROM users WHERE username='admin'" -N --skip-ssl 2>/dev/null)
    
    if [ "$ADMIN_EXISTS" = "0" ]; then
        log_warn "⚠️ admin 用户不存在，创建中..."
        
        # 创建 admin 用户
        mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME --skip-ssl <<EOF
INSERT INTO users (username, password, email, created_at, updated_at)
VALUES ('admin', '$BCRYPT_HASH', 'admin@example.com', NOW(), NOW());
EOF
        
        # 关联 ADMIN 角色
        mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME --skip-ssl <<EOF
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username='admin' AND r.name='ADMIN';
EOF
        
        log_info "✅ admin 用户创建成功"
    else
        log_info "✅ admin 用户已存在"
        
        # 验证密码哈希格式
        HASH=$(mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME \
            -e "SELECT password FROM users WHERE username='admin'" -N --skip-ssl 2>/dev/null)
        
        if [[ ! "$HASH" =~ ^\$2[ab]\$ ]]; then
            log_warn "⚠️ 密码哈希格式错误（非 BCrypt），修复中..."
            
            # 修复密码哈希
            mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME --skip-ssl <<EOF
UPDATE users SET password='$BCRYPT_HASH', updated_at=NOW()
WHERE username='admin';
EOF
            
            log_info "✅ 密码哈希已修复"
        else
            log_info "✅ 密码哈希格式正确"
        fi
    fi
}

# 验证初始化结果
verify_init() {
    log_info "验证初始化结果..."
    
    # 验证 admin 用户
    HASH=$(mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME \
        -e "SELECT password FROM users WHERE username='admin'" -N --skip-ssl 2>/dev/null)
    
    if [[ "$HASH" =~ ^\$2[ab]\$ ]]; then
        log_info "✅ 密码哈希验证通过"
    else
        log_error "❌ 密码哈希验证失败"
        exit 1
    fi
    
    # 验证角色关联
    ROLE_COUNT=$(mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME \
        -e "SELECT COUNT(*) FROM user_roles ur JOIN users u ON ur.user_id=u.id WHERE u.username='admin'" -N --skip-ssl 2>/dev/null)
    
    if [ "$ROLE_COUNT" -gt 0 ]; then
        log_info "✅ 角色关联验证通过"
    else
        log_error "❌ 角色关联验证失败"
        exit 1
    fi
    
    log_info "✅ 所有验证通过"
}

# 主程序
main() {
    echo "========================================"
    echo "  Career Plan 2026 - Staging 数据初始化"
    echo "========================================"
    echo ""
    
    check_db_connection
    init_admin_user
    verify_init
    
    echo ""
    log_info "🎉 初始化完成"
    echo ""
    echo "测试账号：admin / 123456"
    echo "登录 API: POST http://localhost:9997/back-server/api/v1/auth/login"
}

main "$@"
