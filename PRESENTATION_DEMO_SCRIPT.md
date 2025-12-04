# 🎤 Presentation Demo Script
**SecureBank Hub - Backend Demo**

---

## 📋 **Pre-Demo Setup**

### **1. Start the Server**

#### **Option A: Local Development (H2 Database)**
```bash
cd /Users/vorobyevalx/Desktop/Introduction\ Software\ Engineer/CSCI4830AppBE
./mvnw spring-boot:run
```

#### **Option B: Google Cloud E2 Server (Production)**
```bash
# SSH into your E2 instance
ssh your-username@YOUR_E2_IP

# Navigate to project directory
cd /path/to/CSCI4830AppBE

# Set environment variables
export DB_HOST=107.178.210.174
export DB_NAME=securebankdb
export DB_USER=securebank
export DB_PASSWORD=Welcome2025!

# Start with production profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

**Wait for**: `Started SecureBankHubApplication` message

---

## 🎬 **DEMO SCRIPT - Step by Step**

### **Slide 1: Introduction**
> "I'm Aleksey, the backend developer for SecureBank Hub. Today I'll demonstrate our fraud detection backend system built with Spring Boot."

---

### **Demo 1: Health Check Endpoint**
**What to Say:**
> "First, let's verify our backend service is running and healthy."

**Action:**
```bash
curl http://localhost:8080/api/health
# OR if on E2 server:
curl http://YOUR_E2_IP:8080/api/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "service": "SecureBank Hub Backend",
  "timestamp": "2025-10-18T...",
  "version": "0.0.1-SNAPSHOT"
}
```

**What to Say:**
> "Perfect! Our service is UP and running. This health check endpoint is crucial for monitoring and ensures our application is operational."

---

### **Demo 2: User Management**
**What to Say:**
> "Now let's look at our user management system. We can retrieve all users and see user statistics."

**Action 1 - Get All Users:**
```bash
curl http://localhost:8080/api/users
```

**Action 2 - Get User Count:**
```bash
curl http://localhost:8080/api/users/count
```

**What to Say:**
> "We have a RESTful API for user management. Notice the clean JSON responses - this makes it easy for our frontend team to integrate."

---

### **Demo 3: Account Management**
**What to Say:**
> "Let's examine account management. Each user can have multiple accounts, and we can query accounts by user."

**Action:**
```bash
# Get all accounts
curl http://localhost:8080/api/accounts

# Get accounts for a specific user (replace 1 with actual user ID)
curl http://localhost:8080/api/accounts/user/1

# Get account count
curl http://localhost:8080/api/accounts/count
```

**What to Say:**
> "Our account system supports different account types - checking, savings, and business accounts. This flexibility is important for a banking application."

---

### **Demo 4: Transaction History (Core Feature)**
**What to Say:**
> "The core of our application is transaction management with fraud detection capabilities. Let's see all transactions."

**Action 1 - Get All Transactions:**
```bash
curl http://localhost:8080/api/transactions
```

**What to Say:**
> "Each transaction includes comprehensive data: amount, type, merchant information, location, IP address, and most importantly, fraud detection fields."

---

### **Demo 5: Fraud Detection (Key Feature)**
**What to Say:**
> "Now for the key feature - fraud detection. Our system can identify and flag suspicious transactions."

**Action 1 - Get Flagged Transactions:**
```bash
curl http://localhost:8080/api/transactions/fraud/FLAGGED
```

**What to Say:**
> "Here we see transactions that have been flagged as suspicious. Each transaction has a fraud status - PENDING, APPROVED, FLAGGED, BLOCKED, or UNDER_REVIEW."

**Action 2 - Get High-Risk Transactions:**
```bash
curl http://localhost:8080/api/transactions/high-risk/0.7
```

**What to Say:**
> "We can also query transactions by fraud score. This endpoint returns transactions with a fraud score above 0.7, indicating high risk. The fraud score ranges from 0.0 to 1.0, where 1.0 is the highest risk."

**Action 3 - Get Transaction Count:**
```bash
curl http://localhost:8080/api/transactions/count
```

**What to Say:**
> "Our system tracks all transactions and provides analytics. This is essential for fraud detection and compliance."

---

### **Demo 6: Database Console (Optional)**
**What to Say:**
> "For development, we have a web-based database console where we can inspect data directly."

**Action:**
1. Open browser: `http://localhost:8080/h2-console`
2. Enter connection details:
   - JDBC URL: `jdbc:h2:mem:securebankdb`
   - Username: `sa`
   - Password: (leave empty)
3. Click "Connect"
4. Run query: `SELECT * FROM transactions WHERE fraud_status = 'FLAGGED'`

**What to Say:**
> "This console allows us to verify data integrity and test fraud detection queries directly in the database."

---

### **Demo 7: API Endpoint Summary**
**What to Say:**
> "Let me summarize what we've built. We have over 15 REST endpoints covering:"

**List:**
- Health monitoring
- User management (4 endpoints)
- Account management (6 endpoints)
- Transaction management (7+ endpoints with fraud detection)

**What to Say:**
> "All endpoints follow RESTful principles and return JSON responses, making integration straightforward for our frontend team."

---

### **Demo 8: Technology Stack (Quick Overview)**
**What to Say:**
> "Our backend is built with industry-standard technologies:"

**Mention:**
- **Spring Boot 3.4.0** - Rapid development framework
- **Java 17** - Enterprise-grade programming language
- **Spring Data JPA** - Database abstraction layer
- **H2 Database** - For development and testing
- **Google Cloud SQL** - Production MySQL database
- **Spring Security** - Security framework (JWT ready)

---

### **Demo 9: Version Control**
**What to Say:**
> "We maintain our codebase using Git and GitHub for version control."

**Action:**
1. Open browser: `https://github.com/Vorobyevalx/CSCI4830AppBE`
2. Show repository structure
3. Show recent commits

**What to Say:**
> "We use feature branches for development. All our work is documented with clear commit messages, and we have comprehensive documentation files for database access and setup."

---

### **Demo 10: Cloud Infrastructure**
**What to Say:**
> "For production, we've set up Google Cloud SQL MySQL database."

**Mention:**
- **Instance**: securebank-fraud-db
- **Region**: us-central1
- **Public IP**: 107.178.210.174
- **Status**: Operational and tested

**What to Say:**
> "Our application supports multiple profiles - local development with H2, and production with Google Cloud SQL. This allows seamless transition from development to production."

---

## 🎯 **Key Points to Emphasize**

1. **Fraud Detection Focus**: Highlight the fraud detection fields and endpoints
2. **RESTful Design**: Clean API structure, JSON responses
3. **Scalability**: Multi-profile configuration, cloud-ready
4. **Security**: Spring Security configured, JWT ready
5. **Documentation**: Comprehensive guides for team reference

---

## ⚠️ **Troubleshooting Tips**

### **If Server Won't Start:**
```bash
# Check if port 8080 is in use
lsof -i :8080

# Kill process if needed
kill -9 <PID>

# Check Java version
java -version  # Should be 17
```

### **If API Returns 403:**
- Check SecurityConfig.java - endpoints should be permitted
- Verify you're using the correct URL

### **If Database Connection Fails:**
- For local: Check H2 console is accessible
- For production: Verify environment variables are set
- Check Google Cloud SQL instance is running

### **If No Data Appears:**
- Database might be empty (H2 resets on restart)
- Create test data using POST endpoints
- Check H2 console to verify data exists

---

## 📝 **Quick Reference Commands**

```bash
# Health Check
curl http://localhost:8080/api/health

# Users
curl http://localhost:8080/api/users
curl http://localhost:8080/api/users/count

# Accounts
curl http://localhost:8080/api/accounts
curl http://localhost:8080/api/accounts/count

# Transactions
curl http://localhost:8080/api/transactions
curl http://localhost:8080/api/transactions/fraud/FLAGGED
curl http://localhost:8080/api/transactions/high-risk/0.7
curl http://localhost:8080/api/transactions/count

# Pretty print JSON (if jq installed)
curl http://localhost:8080/api/health | jq
```

---

## 🎬 **Closing Statement**

> "In summary, we've built a robust backend system with comprehensive fraud detection capabilities. Our RESTful API is ready for frontend integration, and we have a solid foundation for authentication and security. The system is scalable, well-documented, and production-ready. Thank you!"

---

**Good luck with your presentation! 🚀**

