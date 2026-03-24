#!/bin/bash
APP_NAME="career-plan-staging"
SCRIPT_DIR="$(dirname "$0")"

echo "Restarting $APP_NAME..."

# Stop service
"$SCRIPT_DIR/stop.sh"

# Wait for service to stop completely
echo "Waiting for service to stop..."
sleep 5

# Start service
"$SCRIPT_DIR/start.sh"

echo "Restart complete"
