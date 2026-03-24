-- 测试数据（密码：admin123 的 BCrypt 哈希）
-- 哈希值使用 BCryptPasswordEncoder(10) 生成
-- H2 数据库使用 @Column(name="role_id") 定义的列名
INSERT INTO users (username, password, email, role_id) VALUES
('admin', '$2b$10$AKoLWiRjexPm/ifpA8wnqePTJLygqjHFl3TZ5WqTSXyZscRGKQfvC', 'admin@test.com', 1);
