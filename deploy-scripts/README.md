# Career Plan 后端服务管理脚本

## 特性
- 只管理 career-plan 进程，不影响其他 Java 服务
- 优雅停止 (SIGTERM + 超时 SIGKILL)
- PID 文件管理
- 状态检查 (PID/内存/端口)

## 脚本
- start.sh: 启动服务
- stop.sh: 停止服务
- restart.sh: 重启服务
- status.sh: 检查状态

## 使用
./start.sh
./stop.sh
./restart.sh
./status.sh
