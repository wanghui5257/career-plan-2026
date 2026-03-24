#!/bin/bash
APP_NAME="career-plan-staging"
APP_DIR="/opt/career-plan/staging"
JAR_FILE="$APP_DIR/career-plan-2026-1.0.0.jar"
PID_FILE="$APP_DIR/career-plan.pid"
LOG_FILE="$APP_DIR/app.log"

echo "Starting $APP_NAME..."

# Check if already running
if [ -f "$PID_FILE" ]; then
    PID=$(cat $PID_FILE)
    if kill -0 $PID 2>/dev/null; then
        echo "Service already running with PID $PID"
        exit 1
    else
        echo "Removing stale PID file"
        rm -f $PID_FILE
    fi
fi

# Start service
cd "$APP_DIR"
nohup java -Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
    -jar "$JAR_FILE" --spring.profiles.active=staging > "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

# Wait and verify
sleep 3
if kill -0 $(cat $PID_FILE) 2>/dev/null; then
    echo "$APP_NAME started with PID $(cat $PID_FILE)"
    echo "Log file: $LOG_FILE"
else
    echo "Failed to start service"
    rm -f $PID_FILE
    exit 1
fi
