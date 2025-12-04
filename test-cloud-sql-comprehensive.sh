#!/bin/bash

# Comprehensive Cloud SQL Connection Test
# This script tests various connection scenarios to identify the issue

DB_HOST="107.178.210.174"
DB_NAME="securebankdb"
DB_USER="securebank"
DB_PASSWORD="Welcome2025!"
CONNECTION_NAME="planar-contact-474800-i8:us-central1:securebank-fraud-db"

echo "=========================================="
echo "Comprehensive Cloud SQL Connection Test"
echo "=========================================="
echo ""

# Test 1: IP Reachability
echo "1. Testing IP reachability..."
if ping -c 2 $DB_HOST > /dev/null 2>&1; then
    echo "   ✅ IP is reachable"
else
    echo "   ❌ IP is NOT reachable"
fi
echo ""

# Test 2: Port 3306 Connectivity
echo "2. Testing port 3306 connectivity..."
if nc -zv -w 5 $DB_HOST 3306 2>&1 | grep -q "succeeded"; then
    echo "   ✅ Port 3306 is open"
else
    echo "   ❌ Port 3306 is NOT accessible (timeout or blocked)"
    echo "   ⚠️  This is the main issue - firewall/authorized networks"
fi
echo ""

# Test 3: Check if gcloud is installed
echo "3. Checking for Google Cloud SDK..."
if command -v gcloud &> /dev/null; then
    echo "   ✅ gcloud CLI is installed"
    echo "   Testing Cloud SQL instance status..."
    gcloud sql instances describe securebank-fraud-db --format="value(state,ipAddresses[0].ipAddress)" 2>/dev/null || echo "   ⚠️  Could not query instance (may need authentication)"
else
    echo "   ⚠️  gcloud CLI not installed (optional)"
fi
echo ""

# Test 4: MySQL Client Test
echo "4. Testing MySQL connection..."
if command -v mysql &> /dev/null; then
    mysql -h $DB_HOST -u $DB_USER -p"$DB_PASSWORD" -e "SELECT 1 as test;" $DB_NAME 2>&1 | head -3
    if [ $? -eq 0 ]; then
        echo "   ✅ MySQL connection successful"
    else
        echo "   ❌ MySQL connection failed"
    fi
else
    echo "   ⚠️  MySQL client not installed"
fi
echo ""

# Test 5: Current IP Address
echo "5. Your current IP address:"
CURRENT_IP=$(curl -s https://api.ipify.org)
echo "   $CURRENT_IP"
echo "   ⚠️  Make sure this IP is in Cloud SQL authorized networks!"
echo ""

# Test 6: DNS Resolution
echo "6. Testing DNS resolution..."
if nslookup $DB_HOST > /dev/null 2>&1; then
    echo "   ✅ DNS resolution works"
else
    echo "   ⚠️  DNS resolution issue (may be normal for IP addresses)"
fi
echo ""

# Test 7: Traceroute (if available)
echo "7. Network path to database..."
if command -v traceroute &> /dev/null; then
    echo "   Running traceroute (first 3 hops)..."
    traceroute -m 3 $DB_HOST 2>/dev/null | head -5
elif command -v mtr &> /dev/null; then
    echo "   Running mtr (first 3 hops)..."
    mtr -c 1 -r -n $DB_HOST 2>/dev/null | head -5
else
    echo "   ⚠️  traceroute/mtr not available"
fi
echo ""

# Test 8: SSL Test
echo "8. Testing SSL connection..."
if command -v openssl &> /dev/null; then
    echo | openssl s_client -connect $DB_HOST:3306 -showcerts 2>&1 | head -5
    if [ $? -eq 0 ]; then
        echo "   ✅ SSL port is accessible"
    else
        echo "   ⚠️  SSL test inconclusive"
    fi
else
    echo "   ⚠️  openssl not available"
fi
echo ""

echo "=========================================="
echo "Diagnosis Summary"
echo "=========================================="
echo ""
echo "Most Likely Issue: Port 3306 is blocked by Google Cloud SQL firewall"
echo ""
echo "Solutions (in order of recommendation):"
echo ""
echo "1. ADD YOUR IP TO AUTHORIZED NETWORKS (Quickest Fix)"
echo "   - Go to: https://console.cloud.google.com/sql/instances/securebank-fraud-db/connections"
echo "   - Add network: $CURRENT_IP/32"
echo "   - Wait 1-2 minutes and test again"
echo ""
echo "2. USE CLOUD SQL PROXY (Most Secure)"
echo "   - Download: https://cloud.google.com/sql/docs/mysql/sql-proxy"
echo "   - Run: ./cloud-sql-proxy $CONNECTION_NAME"
echo "   - Connect to localhost:3306 instead"
echo ""
echo "3. TEMPORARY TEST (NOT FOR PRODUCTION)"
echo "   - Add 0.0.0.0/0 to authorized networks (allows all IPs)"
echo "   - Remove immediately after testing!"
echo ""
echo "=========================================="

