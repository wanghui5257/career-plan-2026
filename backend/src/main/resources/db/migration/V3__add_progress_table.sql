-- V3: 添加进度表
-- 用于记录和追踪计划的整体进度

CREATE TABLE IF NOT EXISTS progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    progress_percentage DOUBLE,
    completed_tasks INT,
    total_tasks INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plan_id (plan_id),
    INDEX idx_updated_at (updated_at),
    FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE
);

-- 说明：
-- progress_percentage: 0-100 的进度百分比
-- completed_tasks: 已完成的任务数
-- total_tasks: 总任务数
-- 每次任务状态变更时，更新此表记录
