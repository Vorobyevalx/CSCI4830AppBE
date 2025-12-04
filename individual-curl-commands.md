# Individual Curl Commands for Screenshots

Use these commands one at a time for clean screenshots. Each command is formatted for easy copy-paste.

## 🎯 Priority Commands (Must Have for Presentation)

### 1. Health Check
```bash
curl http://YOUR_E2_IP:8080/api/health | jq
```

### 2. Fraud Detection - Flagged Transactions (KEY FEATURE)
```bash
curl http://YOUR_E2_IP:8080/api/transactions/fraud/FLAGGED | jq
```

### 3. Fraud Detection - High-Risk Transactions (KEY FEATURE)
```bash
curl http://YOUR_E2_IP:8080/api/transactions/high-risk/0.7 | jq
```

---

## 📋 All Commands (For Complete Demo)

### Health & Monitoring
```bash
# Health Check
curl http://YOUR_E2_IP:8080/api/health | jq
```

### User Management
```bash
# Get All Users
curl http://YOUR_E2_IP:8080/api/users | jq

# Get User Count
curl http://YOUR_E2_IP:8080/api/users/count | jq

# Get User by ID (replace 1 with actual ID)
curl http://YOUR_E2_IP:8080/api/users/1 | jq
```

### Account Management
```bash
# Get All Accounts
curl http://YOUR_E2_IP:8080/api/accounts | jq

# Get Account Count
curl http://YOUR_E2_IP:8080/api/accounts/count | jq

# Get Accounts by User (replace 1 with actual user ID)
curl http://YOUR_E2_IP:8080/api/accounts/user/1 | jq
```

### Transaction Management
```bash
# Get All Transactions
curl http://YOUR_E2_IP:8080/api/transactions | jq

# Get Transaction Count
curl http://YOUR_E2_IP:8080/api/transactions/count | jq

# Get Transaction by ID (replace 1 with actual ID)
curl http://YOUR_E2_IP:8080/api/transactions/1 | jq

# Get Transactions by Account (replace 1 with actual account ID)
curl http://YOUR_E2_IP:8080/api/transactions/account/1 | jq
```

### Fraud Detection (KEY FEATURES)
```bash
# Get Flagged Transactions
curl http://YOUR_E2_IP:8080/api/transactions/fraud/FLAGGED | jq

# Get Blocked Transactions
curl http://YOUR_E2_IP:8080/api/transactions/fraud/BLOCKED | jq

# Get High-Risk Transactions (score > 0.7)
curl http://YOUR_E2_IP:8080/api/transactions/high-risk/0.7 | jq

# Get High-Risk Transactions (score > 0.5)
curl http://YOUR_E2_IP:8080/api/transactions/high-risk/0.5 | jq

# Get Recent Transactions (last 24 hours, replace 1 with account ID)
curl http://YOUR_E2_IP:8080/api/transactions/recent/1 | jq
```

---

## 📸 Screenshot Tips

### **Option 1: Terminal Screenshots (Recommended)**
1. Open terminal with nice theme (dark background works well)
2. Make terminal window wide enough to show full JSON
3. Run commands one at a time
4. Use `jq` for pretty JSON formatting
5. Take screenshot of terminal window

### **Option 2: Browser Screenshots**
1. Open browser: `http://YOUR_E2_IP:8080/api/health`
2. Install JSON formatter extension
3. Take screenshot of formatted JSON

### **Option 3: Postman Screenshots**
1. Import endpoints into Postman
2. Run requests
3. Take screenshots of responses

---

## 🎨 Formatting Commands

### Install jq (for pretty JSON)
```bash
# macOS
brew install jq

# Linux
sudo apt-get install jq
```

### Without jq (still works, just not as pretty)
```bash
curl http://YOUR_E2_IP:8080/api/health
```

### With jq (pretty formatted)
```bash
curl http://YOUR_E2_IP:8080/api/health | jq
```

---

## 🎬 Recommended Screenshot Sequence

1. **Health Check** - Shows service is running
2. **Get All Users** - Shows basic CRUD functionality
3. **Get All Transactions** - Shows transaction data
4. **Fraud Flagged** - **KEY FEATURE** - Fraud detection
5. **High-Risk Transactions** - **KEY FEATURE** - Fraud scoring

---

## 💡 Pro Tips

1. **Use a clean terminal** - Clear screen before each command
2. **Wide terminal** - Make it wide enough to show full JSON
3. **Dark theme** - Looks more professional
4. **One command per screenshot** - Easier to read
5. **Highlight the command** - Make sure the curl command is visible
6. **Show the response** - Include the JSON response in screenshot

---

## 🔧 Troubleshooting

### If jq is not installed:
```bash
# The commands will still work, just without pretty formatting
curl http://YOUR_E2_IP:8080/api/health
```

### If you get connection errors:
- Verify your E2 server IP is correct
- Check server is running: `curl http://YOUR_E2_IP:8080/api/health`
- Verify firewall allows port 8080

### If responses are empty:
- Database might be empty
- Create test data first using POST endpoints
- Check H2 console to verify data exists

---

**Replace `YOUR_E2_IP` with your actual Google Cloud E2 server IP address!**

