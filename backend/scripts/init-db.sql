-- Career Plan 2026 Database Initialization
-- Database: career_plan

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'TODO',
    progress INT DEFAULT 0,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    assigned_to VARCHAR(100),
    due_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create progress_reports table
CREATE TABLE IF NOT EXISTS progress_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT,
    user_id BIGINT,
    content TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert default admin user (password: admin123, BCRYPT hash)
INSERT INTO users (username, password, email, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iBT0CkKqQk8rJzKzKzKzKzKzKzKz', 'admin@career-plan.com', 'ADMIN');

-- Insert sample tasks
INSERT INTO tasks (title, description, status, progress, priority, assigned_to) VALUES
('前端界面原型设计', '设计任务看板、可编辑任务、进度图表', 'IN_PROGRESS', 50, 'HIGH', 'alice'),
('后端 API 接口设计', '设计 RESTful API 接口文档', 'DONE', 100, 'HIGH', 'backend-dev'),
('SpringBoot 框架搭建', '搭建 SpringBoot 3.x 项目框架', 'DONE', 100, 'HIGH', 'backend-dev'),
('数据库配置', '配置 MySQL 数据库和初始化', 'DONE', 100, 'HIGH', 'admin'),
('阿里云部署', '配置宝塔面板和部署环境', 'DONE', 100, 'HIGH', 'admin'),
('前后端联调', '完成前后端接口对接', 'TODO', 0, 'MEDIUM', 'alice'),
('测试和优化', '功能测试和性能优化', 'TODO', 0, 'LOW', 'all');
