#!/bin/bash
APP_NAME="career-plan-staging"
APP_DIR="/opt/career-plan"
JAR_FILE="$APP_DIR/staging/career-plan-2026-1.0.0.jar"
PID_FILE="$APP_DIR/staging/staging.pid"
LOG_FILE="$APP_DIR/staging/staging.log"

echo "Starting $APP_NAME (staging profile)..."

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null; then
        echo "Already running (PID: $PID)"
        exit 1
    fi
fi

cd "$APP_DIR/staging"
nohup java -Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
    -jar "$JAR_FILE" --spring.profiles.active=staging > "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

echo "$APP_NAME started!"
echo "PID: $(cat $PID_FILE)"
echo "Port: 9997"
