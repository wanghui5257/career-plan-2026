#!/bin/bash

APP_NAME="career-plan-2026"
APP_DIR="/opt/career-plan"
PID_FILE="$APP_DIR/app.pid"

echo "Stopping $APP_NAME..."

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null; then
        kill $PID
        echo "$APP_NAME stopped (PID: $PID)"
        rm -f "$PID_FILE"
    else
        echo "Application is not running"
        rm -f "$PID_FILE"
    fi
else
    echo "PID file not found"
fi
