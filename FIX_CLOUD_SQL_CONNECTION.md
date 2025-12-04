# 🔧 Fix Google Cloud SQL Connection

## Problem
Connection timeout when trying to connect to Google Cloud SQL from your local machine.

## Solution
Add your IP address to the authorized networks in Google Cloud SQL.

---

## ✅ Step-by-Step Fix

### **Step 1: Get Your IP Address**
Your current IP: **137.48.255.231**

*(If this changes, you'll need to update it in Cloud SQL)*

---

### **Step 2: Add IP to Authorized Networks**

1. **Go to Google Cloud Console**
   - Open: https://console.cloud.google.com/sql
   - Or navigate: **SQL** → **Instances** → **securebank-fraud-db**

2. **Click on your instance**: `securebank-fraud-db`

3. **Go to "Connections" tab**
   - Click on **"Connections"** in the left sidebar
   - Or scroll down to the **"Authorized networks"** section

4. **Add Authorized Network**
   - Click **"Add network"** or **"Add authorized network"**
   - **Network**: `137.48.255.231/32`
     - The `/32` means only this specific IP address
   - **Name**: `My Development Machine` (or any name you prefer)
   - Click **"Add"** or **"Save"**

5. **Wait a few seconds** for the change to propagate

---

### **Step 3: Verify Database and User Exist**

1. **In Cloud SQL Console**, go to **"Databases"** tab
   - Verify `securebankdb` exists
   - If not, click **"Create database"** and create it

2. **Go to "Users"** tab
   - Verify user `securebank` exists
   - If not, click **"Add user account"**:
     - **Username**: `securebank`
     - **Password**: `Welcome2025!`
     - **Host**: `%` (allows from anywhere)

---

### **Step 4: Test Connection**

After adding your IP, test the connection:

```bash
# Test with MySQL client (if installed)
mysql -h 107.178.210.174 -u securebank -p securebankdb
# Enter password: Welcome2025!
```

Or test with your Spring Boot app:

```bash
# Set environment variables
export DB_HOST=107.178.210.174
export DB_NAME=securebankdb
export DB_USER=securebank
export DB_PASSWORD=Welcome2025!

# Start server
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 🔍 Alternative: Check Current Authorized Networks

If you want to see what's currently authorized:

1. Go to Cloud SQL Console
2. Click on your instance
3. Go to "Connections" tab
4. Look at "Authorized networks" section

---

## ⚠️ Important Notes

1. **IP Address Changes**: If your IP changes (e.g., different network), you'll need to update it
2. **Security**: For production, consider using Cloud SQL Proxy instead of public IP
3. **Firewall**: Make sure no local firewall is blocking port 3306

---

## 🚨 If Still Not Working

### Check 1: Verify Instance is Running
- In Cloud SQL Console, check instance status is "RUNNING"

### Check 2: Verify Public IP is Enabled
- In "Connections" tab, make sure "Public IP" is enabled
- If not, you may need to add a public IP

### Check 3: Test with MySQL Client
```bash
# Install MySQL client if needed (macOS)
brew install mysql-client

# Test connection
mysql -h 107.178.210.174 -u securebank -p securebankdb
```

### Check 4: Check Firewall Rules
```bash
# Check if port 3306 is blocked locally
telnet 107.178.210.174 3306
```

---

## ✅ Quick Checklist

- [ ] Added IP `137.48.255.231/32` to authorized networks
- [ ] Database `securebankdb` exists
- [ ] User `securebank` exists with password `Welcome2025!`
- [ ] Public IP is enabled on instance
- [ ] Instance status is "RUNNING"
- [ ] Tested connection with Spring Boot

---

**After completing these steps, try starting your server again!**

