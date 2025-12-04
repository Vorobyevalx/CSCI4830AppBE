#!/bin/bash

# Cloud SQL Connection Diagnostic Script

DB_HOST="107.178.210.174"
DB_NAME="securebankdb"
DB_USER="securebank"
DB_PASSWORD="Welcome2025!"

echo "=========================================="
echo "Cloud SQL Connection Diagnostics"
echo "=========================================="
echo ""

echo "1. Testing IP reachability..."
if ping -c 2 $DB_HOST > /dev/null 2>&1; then
    echo "   ✅ IP is reachable"
else
    echo "   ❌ IP is NOT reachable"
fi
echo ""

echo "2. Testing port 3306 connectivity..."
if nc -zv -w 5 $DB_HOST 3306 2>&1 | grep -q "succeeded"; then
    echo "   ✅ Port 3306 is open"
else
    echo "   ❌ Port 3306 is NOT accessible (timeout or blocked)"
    echo "   This is likely the issue!"
fi
echo ""

echo "3. Testing MySQL connection..."
mysql -h $DB_HOST -u $DB_USER -p"$DB_PASSWORD" -e "SELECT 1;" $DB_NAME 2>&1 | head -3
if [ $? -eq 0 ]; then
    echo "   ✅ MySQL connection successful"
else
    echo "   ❌ MySQL connection failed"
    echo ""
    echo "   Common issues:"
    echo "   - IP address not in authorized networks"
    echo "   - Firewall blocking port 3306"
    echo "   - Database/user doesn't exist"
    echo "   - Wrong credentials"
fi
echo ""

echo "4. Your current IP address:"
CURRENT_IP=$(curl -s https://api.ipify.org)
echo "   $CURRENT_IP"
echo ""
echo "   Make sure this IP is added to Cloud SQL authorized networks!"
echo ""

echo "=========================================="
echo "Next Steps:"
echo "=========================================="
echo "1. Go to: https://console.cloud.google.com/sql/instances/securebank-fraud-db/connections"
echo "2. Add authorized network: $CURRENT_IP/32"
echo "3. Verify database 'securebankdb' exists"
echo "4. Verify user 'securebank' exists"
echo "5. Run this script again to test"
echo ""


