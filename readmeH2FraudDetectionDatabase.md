# H2 Fraud Detection Database - Complete Setup
securebank-fraud-db
Welcome2025!
securebank
Welcome2025!
Connection name:
planar-contact-474800-i8:us-central1:securebank-fraud-db
Public IP address:
107.178.210.174
## 🎯 **Project Overview**

This document outlines the complete fraud detection database setup for the SecureBank Hub backend application. The database is designed specifically for fraud detection and transaction analysis, with a focus on identifying suspicious activities.

## 🗄️ **Database Structure**

### **Core Entities Created:**

#### **1. User Entity**
- **Purpose**: User management with roles
- **Key Fields**: username, email, password, firstName, lastName, role, isActive
- **Roles**: CUSTOMER, ADMIN, BANKER
- **Location**: `src/main/java/com/securebank/hub/model/User.java`

#### **2. Account Entity**
- **Purpose**: Account information linked to users
- **Key Fields**: accountNumber, user, accountType, balance, isActive
- **Account Types**: CHECKING, SAVINGS, BUSINESS
- **Location**: `src/main/java/com/securebank/hub/model/Account.java`

#### **3. Transaction Entity (Fraud-Focused)**
- **Purpose**: Transaction history with comprehensive fraud detection fields
- **Key Fields**:
  - **Basic**: account, transactionType, amount, description
  - **Merchant Info**: merchantName, merchantCategory
  - **Location Data**: location, ipAddress, deviceFingerprint
  - **Fraud Detection**: fraudStatus, fraudScore, fraudReasons
  - **Timestamps**: transactionTimestamp, createdAt
- **Location**: `src/main/java/com/securebank/hub/model/Transaction.java`

### **Enums Created:**

#### **TransactionType**
- DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN, PURCHASE, REFUND, FEE
- **Location**: `src/main/java/com/securebank/hub/model/TransactionType.java`

#### **FraudStatus**
- PENDING, APPROVED, FLAGGED, BLOCKED, UNDER_REVIEW
- **Location**: `src/main/java/com/securebank/hub/model/FraudStatus.java`

#### **AccountType**
- CHECKING, SAVINGS, BUSINESS
- **Location**: `src/main/java/com/securebank/hub/model/AccountType.java`

#### **UserRole**
- CUSTOMER, ADMIN, BANKER
- **Location**: `src/main/java/com/securebank/hub/model/UserRole.java`

## 🔍 **Fraud Detection Features**

### **Fraud Detection Fields:**
- **Fraud Status**: PENDING, APPROVED, FLAGGED, BLOCKED, UNDER_REVIEW
- **Fraud Score**: 0.0 to 1.0 (risk level)
- **Fraud Reasons**: Text description of why flagged
- **Location**: Transaction location for geographic analysis
- **IP Address**: For IP-based fraud detection
- **Device Fingerprint**: Device identification
- **Merchant Info**: Name and category for pattern analysis

### **Fraud Detection Queries Available:**
```sql
-- High-risk transactions
SELECT * FROM TRANSACTIONS WHERE FRAUD_SCORE > 0.7;

-- Blocked transactions
SELECT * FROM TRANSACTIONS WHERE FRAUD_STATUS = 'BLOCKED';

-- Transactions by location
SELECT LOCATION, COUNT(*) as TRANSACTION_COUNT, AVG(FRAUD_SCORE) as AVG_FRAUD_SCORE
FROM TRANSACTIONS GROUP BY LOCATION;

-- Recent transactions by account
SELECT * FROM TRANSACTIONS WHERE ACCOUNT_ID = ? AND TRANSACTION_TIMESTAMP >= ?;

-- Transactions by IP address
SELECT * FROM TRANSACTIONS WHERE IP_ADDRESS = ? AND TRANSACTION_TIMESTAMP >= ?;
```

## 📊 **Repositories Created**

### **1. UserRepository**
- **Location**: `src/main/java/com/securebank/hub/repository/UserRepository.java`
- **Methods**: findByUsername, findByEmail, existsByUsername, existsByEmail

### **2. AccountRepository**
- **Location**: `src/main/java/com/securebank/hub/repository/AccountRepository.java`
- **Methods**: findByAccountNumber, findByUser, findByUserId, findByIsActive

### **3. TransactionRepository (Fraud-Focused)**
- **Location**: `src/main/java/com/securebank/hub/repository/TransactionRepository.java`
- **Fraud Detection Methods**:
  - `findFraudulentTransactions(status)` - Get transactions by fraud status
  - `findHighRiskTransactions(threshold)` - Get high-risk transactions
  - `findRecentTransactionsByAccount(accountId, since)` - Recent transactions
  - `findTransactionsByIpAddress(ipAddress, since)` - IP-based analysis
  - `findTransactionsByLocation(location, since)` - Location-based analysis
  - `countRecentTransactionsByAccount(accountId, since)` - Transaction frequency
  - `sumRecentTransactionsByAccountAndType(accountId, since, type)` - Amount analysis

## 🎮 **API Endpoints Created**

### **User Management:**
```bash
GET /api/users                    # List all users
GET /api/users/{id}              # Get user by ID
POST /api/users                  # Create new user
GET /api/users/count             # Count total users
```

### **Account Management:**
```bash
GET /api/accounts                    # List all accounts
GET /api/accounts/{id}              # Get account by ID
GET /api/accounts/user/{userId}     # Get accounts by user
GET /api/accounts/number/{number}   # Get account by number
POST /api/accounts                  # Create new account
GET /api/accounts/count             # Count total accounts
```

### **Transaction Management (Fraud-Focused):**
```bash
GET /api/transactions                    # List all transactions
GET /api/transactions/{id}              # Get transaction by ID
GET /api/transactions/account/{id}      # Get transactions by account
GET /api/transactions/fraud/{status}    # Get transactions by fraud status
GET /api/transactions/high-risk/{score} # Get high-risk transactions
GET /api/transactions/recent/{id}       # Get recent transactions
POST /api/transactions                  # Create new transaction
PUT /api/transactions/{id}/fraud-status # Update fraud status
GET /api/transactions/count             # Count total transactions
```

## 🧪 **Test Data Created**

### **Sample Data:**
1. **User**: John Doe (ID: 1)
   - Username: john_doe
   - Email: john@example.com
   - Role: CUSTOMER

2. **Account**: ACC001 (ID: 1)
   - Account Number: ACC001
   - Type: CHECKING
   - Balance: $5,000.00
   - User: John Doe

3. **Transactions**:
   - ✅ **Approved**: $150 grocery purchase (fraud score: 0.1)
     - Merchant: SafeMart, Location: New York, NY
     - Status: APPROVED
   - ⚠️ **Flagged**: $2,500 electronics (fraud score: 0.8)
     - Merchant: ElectroWorld, Location: Los Angeles, CA
     - Status: FLAGGED
     - Reasons: "High amount, unusual location, new device"
   - 🚫 **Blocked**: $5,000 ATM withdrawal (fraud score: 0.95)
     - Merchant: ATM Network, Location: Miami, FL
     - Status: BLOCKED
     - Reasons: "Maximum withdrawal amount, suspicious location, high-risk IP"

## 🔧 **Configuration**

### **Security Configuration:**
- **File**: `src/main/java/com/securebank/hub/config/SecurityConfig.java`
- **Access**: All API endpoints and H2 console are open for development
- **H2 Console**: Accessible at `/h2-console`

### **Application Properties:**
- **File**: `src/main/resources/application.yml`
- **Profile**: `local` (H2 in-memory database)
- **Database**: `jdbc:h2:mem:securebankdb`
- **H2 Console**: Enabled at `/h2-console`

## 🗄️ **H2 Database Console Access**

### **Access Information:**
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:securebankdb
User Name: sa
Password: (leave empty)
```

### **Useful Queries for Fraud Analysis:**
```sql
-- View all transactions with fraud info
SELECT ID, TRANSACTION_TYPE, AMOUNT, FRAUD_STATUS, FRAUD_SCORE, FRAUD_REASONS, LOCATION, IP_ADDRESS
FROM TRANSACTIONS;

-- High-risk transactions
SELECT * FROM TRANSACTIONS WHERE FRAUD_SCORE > 0.7;

-- Blocked transactions
SELECT * FROM TRANSACTIONS WHERE FRAUD_STATUS = 'BLOCKED';

-- Transactions by location
SELECT LOCATION, COUNT(*) as TRANSACTION_COUNT, AVG(FRAUD_SCORE) as AVG_FRAUD_SCORE
FROM TRANSACTIONS GROUP BY LOCATION;

-- Recent transactions (last 24 hours)
SELECT * FROM TRANSACTIONS WHERE TRANSACTION_TIMESTAMP >= DATEADD('HOUR', -24, NOW());

-- Account summary
SELECT a.ACCOUNT_NUMBER, u.USERNAME, a.BALANCE, COUNT(t.ID) as TRANSACTION_COUNT
FROM ACCOUNTS a
JOIN USERS u ON a.USER_ID = u.ID
LEFT JOIN TRANSACTIONS t ON a.ID = t.ACCOUNT_ID
GROUP BY a.ID, a.ACCOUNT_NUMBER, u.USERNAME, a.BALANCE;
```

## 🚀 **Current Status**

### **✅ Completed:**
- ✅ **Database Structure**: All entities and relationships created
- ✅ **Fraud Detection Fields**: Comprehensive fraud analysis capabilities
- ✅ **Repositories**: Data access layer with fraud-focused queries
- ✅ **API Endpoints**: RESTful endpoints for all operations
- ✅ **Test Data**: Sample data with different fraud scenarios
- ✅ **H2 Console**: Database access and querying capability
- ✅ **Security Configuration**: Development-friendly access settings

### **📊 Database Statistics:**
- **Tables**: 3 (users, accounts, transactions)
- **Users**: 1 (John Doe)
- **Accounts**: 1 (ACC001)
- **Transactions**: 3 (1 approved, 1 flagged, 1 blocked)
- **Fraud Detection Fields**: 6 (status, score, reasons, location, IP, device)

## 🎯 **Next Steps**

### **Ready for Production:**
1. **Google Cloud SQL Setup** - Migrate to production database
2. **Fraud Detection Logic** - Implement business rules
3. **Authentication** - Add JWT security
4. **Role-Based Access** - Implement user permissions
5. **API Documentation** - Add Swagger/OpenAPI docs

### **Fraud Detection Enhancements:**
1. **Machine Learning Integration** - Add ML-based fraud scoring
2. **Real-time Monitoring** - Implement transaction monitoring
3. **Alert System** - Create fraud alerts and notifications
4. **Reporting Dashboard** - Build fraud analytics dashboard

## 📝 **File Structure**

```
src/main/java/com/securebank/hub/
├── model/
│   ├── User.java
│   ├── UserRole.java
│   ├── Account.java
│   ├── AccountType.java
│   ├── Transaction.java
│   ├── TransactionType.java
│   └── FraudStatus.java
├── repository/
│   ├── UserRepository.java
│   ├── AccountRepository.java
│   └── TransactionRepository.java
├── controller/
│   ├── UserController.java
│   ├── AccountController.java
│   └── TransactionController.java
└── config/
    └── SecurityConfig.java
```

## 🔗 **Related Documentation**

- **H2 Console Access**: `readmeH2Access.md`
- **Project Gameplan**: `gameplan.md`
- **Application Properties**: `src/main/resources/application.yml`

---

**Last Updated**: October 18, 2025  
**Status**: ✅ Complete - Ready for Google Cloud SQL Migration
