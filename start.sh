#!/bin/bash

APP_NAME="career-plan-2026"
APP_DIR="/opt/career-plan"
JAR_FILE="$APP_DIR/backend/target/${APP_NAME}-1.0.0.jar"
PID_FILE="$APP_DIR/app.pid"
LOG_FILE="$APP_DIR/app.log"

echo "Starting $APP_NAME..."

# Check if already running
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null; then
        echo "Application is already running (PID: $PID)"
        exit 1
    fi
fi

# Start the application
cd "$APP_DIR/backend"
nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

echo "$APP_NAME started successfully!"
echo "PID: $(cat $PID_FILE)"
echo "Logs: $LOG_FILE"
