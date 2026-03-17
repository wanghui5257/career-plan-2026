-- V4: 添加导出历史表
-- 用于记录用户的导出操作历史

CREATE TABLE IF NOT EXISTS export_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT,
    file_id VARCHAR(64) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_format VARCHAR(20) NOT NULL,  -- CSV, EXCEL, JSON
    file_size BIGINT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    downloaded_at TIMESTAMP,
    is_expired BOOLEAN DEFAULT FALSE,
    INDEX idx_user_id (user_id),
    INDEX idx_file_id (file_id),
    INDEX idx_expires_at (expires_at),
    INDEX idx_is_expired (is_expired)
);

-- 说明：
-- file_id: 唯一文件标识符（UUID）
-- generated_at: 文件生成时间
-- expires_at: 文件过期时间（7 天后）
-- 定时任务每天清理过期记录
