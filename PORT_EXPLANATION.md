# Port Configuration Explanation

## Ports in This Project

### 1. **Port 3306** - MySQL Database (Cloud SQL)
- **Purpose**: Database connection port
- **Used by**: Spring Boot application to connect to Google Cloud SQL
- **Configuration**: `jdbc:mysql://107.178.210.174:3306/securebankdb`
- **Status**: ✅ Correct port for MySQL

### 2. **Port 8080** - Spring Boot Backend API
- **Purpose**: REST API server
- **Used by**: Frontend and API clients
- **Configuration**: Default Spring Boot port
- **Status**: ✅ Correct port for backend

### 3. **Port 3000** - Frontend Application
- **Purpose**: React/Node.js frontend (if running locally)
- **Used by**: Browser to access the web application
- **Configuration**: Frontend team's choice
- **Status**: Not related to database connection

## Current Database Connection Issue

**We ARE using the correct port (3306) for MySQL.**

The problem is NOT the port number - it's that **port 3306 is blocked by Google Cloud SQL's firewall**.

## Why Port 3306?

- MySQL's default port is **3306**
- Google Cloud SQL uses port **3306** for MySQL connections
- This is the industry standard

## The Real Issue

The connection is timing out because:
1. ✅ Port 3306 is correct
2. ✅ IP address is correct (107.178.210.174)
3. ❌ **Your IP is not in Cloud SQL's authorized networks**

## Solution

Add your IP to Cloud SQL authorized networks:
1. Go to: https://console.cloud.google.com/sql/instances/securebank-fraud-db/connections
2. Add network: `137.48.255.231/32`
3. Save and wait 1-2 minutes

Then the connection to port 3306 will work!

