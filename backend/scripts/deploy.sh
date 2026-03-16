#!/bin/bash

# Career Plan 2026 - Deployment Script
# Execute this on the server (47.115.63.159)

set -e

echo "=========================================="
echo "  Career Plan 2026 - Deployment Script"
echo "=========================================="

# Configuration
APP_NAME="career-plan-2026"
APP_DIR="/opt/career-plan"
DB_NAME="career_plan"
DB_USER="career_user"
DB_PASS="CareerPlan2026!"
JWT_SECRET="v9Kx+L2RqP7mZ4wE1tY6uI8oA3sD5fG0hJ9kL1mN4pQ="

echo ""
echo "Step 1: Install Maven..."
apt update
apt install -y maven
mvn -version

echo ""
echo "Step 2: Create project directory..."
mkdir -p "$APP_DIR"
cd "$APP_DIR"

echo ""
echo "Step 3: Clone code from GitHub..."
git clone -b feature/backend-dev https://github.com/wanghui5257/career-plan-2026.git .

echo ""
echo "Step 4: Import database schema..."
mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" < backend/scripts/init-db.sql
echo "Database imported successfully!"

echo ""
echo "Step 5: Build project with Maven..."
cd backend
mvn clean package -DskipTests

echo ""
echo "Step 6: Setup startup scripts..."
cd /opt/career-plan
cp backend/scripts/start.sh .
cp backend/scripts/stop.sh .
chmod +x start.sh stop.sh

echo ""
echo "Step 7: Start application..."
./start.sh

echo ""
echo "Step 8: Verify deployment..."
sleep 5
curl http://localhost:8080/api/health

echo ""
echo "=========================================="
echo "  Deployment Complete!"
echo "=========================================="
echo ""
echo "API Health: http://localhost:8080/api/health"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Public URL: http://plan.shujuyunxiang.com"
echo ""
echo "Logs: /opt/career-plan/app.log"
echo "PID: $(cat /opt/career-plan/app.pid)"
echo ""
