#!/bin/bash
APP_NAME="career-plan-production"
APP_DIR="/opt/career-plan"
JAR_FILE="$APP_DIR/career-plan.jar"
PID_FILE="$APP_DIR/production.pid"
LOG_FILE="$APP_DIR/production.log"

echo "Starting $APP_NAME (production profile)..."

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null; then
        echo "Already running (PID: $PID)"
        exit 1
    fi
fi

cd "$APP_DIR"
nohup java -Xms1g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
    -jar "$JAR_FILE" --spring.profiles.active=production > "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

echo "$APP_NAME started!"
echo "PID: $(cat $PID_FILE)"
echo "Port: 9999"
