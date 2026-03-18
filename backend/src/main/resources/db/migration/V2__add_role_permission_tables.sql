-- 角色权限管理数据库表结构

-- 权限表
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '权限名',
    description VARCHAR(255) COMMENT '权限描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名',
    description VARCHAR(255) COMMENT '角色描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 角色权限关联表（多对多）
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL COMMENT '角色 ID',
    permission_id BIGINT NOT NULL COMMENT '权限 ID',
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 用户角色关联表（多对一，一个用户一个角色）
-- 注意：users 表已有 role_id 字段，通过 User.role 关联

-- 插入默认权限
INSERT INTO permissions (name, description) VALUES
('PLAN_CREATE', '创建计划'),
('PLAN_VIEW', '查看计划'),
('PLAN_UPDATE', '更新计划'),
('PLAN_DELETE', '删除计划'),
('TASK_CREATE', '创建任务'),
('TASK_VIEW', '查看任务'),
('TASK_UPDATE', '更新任务'),
('TASK_DELETE', '删除任务'),
('PROGRESS_VIEW', '查看进度'),
('PROGRESS_CREATE', '创建进度报告'),
('USER_MANAGE', '用户管理'),
('ROLE_MANAGE', '角色管理')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 插入默认角色
INSERT INTO roles (name, description) VALUES
('ADMIN', '管理员 - 所有权限'),
('PLAN_CREATOR', '计划创建者 - 创建和分配计划'),
('SUPERVISOR', '监督执行者 - 查看进度、发送提醒'),
('EXECUTOR', '任务执行者 - 查看任务、更新进度'),
('WORKER', 'Worker - API 访问、任务执行')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 关联角色和权限
-- ADMIN: 所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ADMIN' AND p.name IN 
('PLAN_CREATE', 'PLAN_VIEW', 'PLAN_UPDATE', 'PLAN_DELETE',
 'TASK_CREATE', 'TASK_VIEW', 'TASK_UPDATE', 'TASK_DELETE',
 'PROGRESS_VIEW', 'PROGRESS_CREATE', 'USER_MANAGE', 'ROLE_MANAGE')
ON DUPLICATE KEY UPDATE role_id=role_id;

-- PLAN_CREATOR: 计划相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'PLAN_CREATOR' AND p.name IN 
('PLAN_CREATE', 'PLAN_VIEW', 'PLAN_UPDATE', 'TASK_CREATE', 'TASK_VIEW')
ON DUPLICATE KEY UPDATE role_id=role_id;

-- SUPERVISOR: 查看权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'SUPERVISOR' AND p.name IN 
('PLAN_VIEW', 'TASK_VIEW', 'PROGRESS_VIEW')
ON DUPLICATE KEY UPDATE role_id=role_id;

-- EXECUTOR: 执行权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'EXECUTOR' AND p.name IN 
('PLAN_VIEW', 'TASK_VIEW', 'TASK_UPDATE', 'PROGRESS_CREATE')
ON DUPLICATE KEY UPDATE role_id=role_id;

-- WORKER: Worker 权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'WORKER' AND p.name IN 
('PLAN_VIEW', 'TASK_VIEW', 'TASK_UPDATE', 'PROGRESS_CREATE')
ON DUPLICATE KEY UPDATE role_id=role_id;

-- 更新 users 表，添加 role_id 字段（如果不存在）
ALTER TABLE users ADD COLUMN IF NOT EXISTS role_id BIGINT COMMENT '角色 ID';
ALTER TABLE users ADD CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id);

-- 将 admin 用户设置为 ADMIN 角色
UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'ADMIN') WHERE username = 'admin';
