# 📸 Manual Screenshot Guide - Step by Step

## ✅ Step 1: Start Your Server

### Open Terminal and Run:

```bash
# Navigate to your project
cd "/Users/vorobyevalx/Desktop/Introduction Software Engineer/CSCI4830AppBE"

# Set environment variables for Google Cloud SQL
export DB_HOST=107.178.210.174
export DB_NAME=securebankdb
export DB_USER=securebank
export DB_PASSWORD=Welcome2025!

# Start the server with production profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

**Wait for**: `Started SecureBankHubApplication` message

**Keep this terminal running!** Open a NEW terminal window for the curl commands.

---

## 📸 Step 2: Take Screenshots One by One

### **Open a NEW Terminal Window** (keep server running in first terminal)

### **Screenshot 1: Health Check** ✅

**Command:**
```bash
curl http://localhost:8080/api/health | jq
```

**What to capture:**
- The curl command
- The JSON response showing status: "UP"

**Take screenshot** 📷

---

### **Screenshot 2: Get All Users** ✅

**Command:**
```bash
curl http://localhost:8080/api/users | jq
```

**What to capture:**
- The curl command
- The JSON array of users (or empty array if no users)

**Take screenshot** 📷

---

### **Screenshot 3: Get User Count** ✅

**Command:**
```bash
curl http://localhost:8080/api/users/count | jq
```

**What to capture:**
- The curl command
- The number response (e.g., `0` or `3`)

**Take screenshot** 📷

---

### **Screenshot 4: Get All Accounts** ✅

**Command:**
```bash
curl http://localhost:8080/api/accounts | jq
```

**What to capture:**
- The curl command
- The JSON array of accounts

**Take screenshot** 📷

---

### **Screenshot 5: Get Account Count** ✅

**Command:**
```bash
curl http://localhost:8080/api/accounts/count | jq
```

**What to capture:**
- The curl command
- The number response

**Take screenshot** 📷

---

### **Screenshot 6: Get All Transactions** ✅

**Command:**
```bash
curl http://localhost:8080/api/transactions | jq
```

**What to capture:**
- The curl command
- The JSON array of transactions

**Take screenshot** 📷

---

### **Screenshot 7: Fraud Detection - Flagged Transactions** ⭐ KEY FEATURE

**Command:**
```bash
curl http://localhost:8080/api/transactions/fraud/FLAGGED | jq
```

**What to capture:**
- The curl command
- The JSON response (may be empty array if no flagged transactions)

**This is a KEY FEATURE - make sure it's clear!**

**Take screenshot** 📷

---

### **Screenshot 8: Fraud Detection - High-Risk Transactions** ⭐ KEY FEATURE

**Command:**
```bash
curl http://localhost:8080/api/transactions/high-risk/0.7 | jq
```

**What to capture:**
- The curl command
- The JSON response showing high-risk transactions

**This is a KEY FEATURE - make sure it's clear!**

**Take screenshot** 📷

---

### **Screenshot 9: Get Transaction Count** ✅

**Command:**
```bash
curl http://localhost:8080/api/transactions/count | jq
```

**What to capture:**
- The curl command
- The number response

**Take screenshot** 📷

---

## 💡 Tips for Better Screenshots

1. **Clear terminal before each command:**
   ```bash
   clear
   ```

2. **Use a clean terminal theme** (dark background looks professional)

3. **Make terminal wide enough** to show full JSON without wrapping

4. **Show both command AND response** in the screenshot

5. **If jq is not installed:**
   ```bash
   brew install jq
   ```
   Or use without jq (still works, just not as pretty):
   ```bash
   curl http://localhost:8080/api/health
   ```

---

## 🎯 Priority Screenshots (Must Have)

1. ✅ Health Check
2. ⭐ Fraud Detection - Flagged (KEY)
3. ⭐ Fraud Detection - High-Risk (KEY)
4. ✅ Get All Transactions
5. ✅ Get All Users

---

## 🔧 Troubleshooting

### If server won't start:
- Check if port 8080 is in use: `lsof -i :8080`
- Kill process if needed: `kill -9 <PID>`

### If you get connection errors:
- Make sure server is running in first terminal
- Wait for "Started SecureBankHubApplication" message

### If responses are empty arrays:
- That's okay! It means database is empty
- You can still show the API structure
- Or create test data first (optional)

---

## ✅ Checklist

- [ ] Server started with production profile
- [ ] Environment variables set
- [ ] New terminal opened for curl commands
- [ ] Screenshot 1: Health Check
- [ ] Screenshot 2: Get All Users
- [ ] Screenshot 3: User Count
- [ ] Screenshot 4: Get All Accounts
- [ ] Screenshot 5: Account Count
- [ ] Screenshot 6: Get All Transactions
- [ ] Screenshot 7: Fraud Flagged ⭐
- [ ] Screenshot 8: High-Risk Transactions ⭐
- [ ] Screenshot 9: Transaction Count

---

**Ready? Let's start with Step 1!** 🚀

