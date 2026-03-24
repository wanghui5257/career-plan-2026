#!/bin/bash
APP_NAME="career-plan-staging"
APP_DIR="/opt/career-plan/staging"
PID_FILE="$APP_DIR/career-plan.pid"

echo "Stopping $APP_NAME..."

if [ -f "$PID_FILE" ]; then
    PID=$(cat $PID_FILE)
    if kill -0 $PID 2>/dev/null; then
        echo "Sending SIGTERM to PID $PID..."
        kill $PID  # SIGTERM
        
        # Wait for graceful shutdown (max 30 seconds)
        for i in {1..30}; do
            if ! kill -0 $PID 2>/dev/null; then
                echo "Service stopped gracefully"
                rm -f $PID_FILE
                exit 0
            fi
            sleep 1
        done
        
        # Force kill if still running
        if kill -0 $PID 2>/dev/null; then
            echo "Service did not stop gracefully, sending SIGKILL..."
            kill -9 $PID
            sleep 2
        fi
        
        rm -f $PID_FILE
        echo "Service stopped"
    else
        echo "Service not running (stale PID file)"
        rm -f $PID_FILE
    fi
else
    # Fallback: find by jar name (only career-plan, not other java processes)
    echo "PID file not found, searching by process name..."
    PID=$(pgrep -f "career-plan-2026.*\.jar")
    if [ -n "$PID" ]; then
        echo "Found career-plan process: $PID"
        kill $PID
        sleep 5
        if kill -0 $PID 2>/dev/null; then
            kill -9 $PID
        fi
        echo "Service stopped (found by process name)"
    else
        echo "Service not running"
    fi
fi
