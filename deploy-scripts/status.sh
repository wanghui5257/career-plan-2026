#!/bin/bash
APP_NAME="career-plan-staging"
APP_DIR="/opt/career-plan/staging"
PID_FILE="$APP_DIR/career-plan.pid"
PORT=9997

echo "=== $APP_NAME Status ==="

if [ -f "$PID_FILE" ]; then
    PID=$(cat $PID_FILE)
    if kill -0 $PID 2>/dev/null; then
        echo "Status: RUNNING"
        echo "PID: $PID"
        echo ""
        
        # Show memory usage
        echo "Memory Usage:"
        ps -o pid,rss,vsz,%mem,%cpu -p $PID 2>/dev/null | tail -1
        echo ""
        
        # Check port
        echo "Port $PORT:"
        if netstat -tlnp 2>/dev/null | grep -q ":$PORT"; then
            echo "  Listening: YES"
        else
            echo "  Listening: NO"
        fi
        echo ""
        
        # Show log tail
        echo "Recent logs:"
        tail -5 "$APP_DIR/app.log" 2>/dev/null
    else
        echo "Status: NOT RUNNING (stale PID file)"
        echo "PID file exists but process $PID is not running"
    fi
else
    echo "Status: NOT RUNNING"
    echo "PID file not found"
    
    # Check if running anyway
    PID=$(pgrep -f "career-plan-2026.*\.jar")
    if [ -n "$PID" ]; then
        echo ""
        echo "Warning: Found career-plan process without PID file"
        echo "PID: $PID"
    fi
fi
