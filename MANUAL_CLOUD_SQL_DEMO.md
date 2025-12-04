# Manual Cloud SQL Connection Demo - Step by Step

## Step 1: Stop Any Running Servers
**Command to type:**
```bash
lsof -ti:8080 | xargs kill -9
```

**What to screenshot:**
- Terminal showing the kill command executed (or "no process found" if nothing was running)

---

## Step 2: Set Environment Variables
**Commands to type (one at a time):**
```bash
export DB_HOST="107.178.210.174"
export DB_NAME="securebankdb"
export DB_USER="securebank"
export DB_PASSWORD='Welcome2025!'
```

**⚠️ Note:** Use **single quotes** for the password because it contains `!` which can cause issues in zsh.

**Verify they're set:**
```bash
echo "DB_HOST=$DB_HOST"
echo "DB_NAME=$DB_NAME"
echo "DB_USER=$DB_USER"
echo "DB_PASSWORD=$DB_PASSWORD"
```

**What to screenshot:**
- Terminal showing all environment variables set correctly

---

## Step 3: Test Port Connectivity
**Command to type:**
```bash
nc -zv -w 5 107.178.210.174 3306
```

**What to screenshot:**
- Terminal showing "Connection to 107.178.210.174 port 3306 [tcp/mysql] succeeded!"

---

## Step 4: Start Spring Boot with Production Profile
**Command to type:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

**What to screenshot:**
- Terminal showing Spring Boot starting up
- Look for: "The following 1 profile is active: 'prod'"
- Look for: "HikariPool-1 - Starting..."
- Look for: "Started SecureBankHubApplication" (success message)
- **Wait for the "Started" message before proceeding!**

---

## Step 5: Test Health Endpoint
**Open a NEW terminal window/tab** (keep the server running in the first one)

**Command to type:**
```bash
curl http://localhost:8080/api/health
```

**What to screenshot:**
- Terminal showing the JSON response with status "UP"

---

## Step 6: Test Database Connection - User Count
**Command to type:**
```bash
curl http://localhost:8080/api/users/count
```

**What to screenshot:**
- Terminal showing "0" (or the count if you have data)

---

## Step 7: Test More Endpoints
**Commands to type (one at a time):**
```bash
curl http://localhost:8080/api/users
```

**What to screenshot:**
- Terminal showing empty array `[]` or list of users

```bash
curl http://localhost:8080/api/accounts
```

**What to screenshot:**
- Terminal showing empty array `[]` or list of accounts

```bash
curl http://localhost:8080/api/transactions
```

**What to screenshot:**
- Terminal showing empty array `[]` or list of transactions

---

## Step 8: Test with jq (if installed) for Pretty Output
**Command to type:**
```bash
curl -s http://localhost:8080/api/health | jq
```

**What to screenshot:**
- Terminal showing nicely formatted JSON (if jq is installed)
- Or just the raw JSON if jq is not installed

---

## Summary of Screenshots Needed:
1. ✅ Environment variables set
2. ✅ Port connectivity test (port 3306 accessible)
3. ✅ Spring Boot starting up (showing "prod" profile)
4. ✅ Spring Boot started successfully
5. ✅ Health endpoint response
6. ✅ User count endpoint
7. ✅ Users endpoint
8. ✅ Accounts endpoint
9. ✅ Transactions endpoint
10. ✅ Pretty JSON output (optional)

---

## Tips:
- Keep the server running in one terminal
- Use a second terminal for curl commands
- Wait for "Started SecureBankHubApplication" before testing endpoints
- If something fails, check the server terminal for error messages

