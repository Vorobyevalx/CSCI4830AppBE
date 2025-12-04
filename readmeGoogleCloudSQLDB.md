# Google Cloud SQL Database - Complete Setup Guide

## 🔗 **Connection Information**

**Connection name:** `planar-contact-474800-i8:us-central1:securebank-fraud-db`  
**Public IP address:** `107.178.210.174`

---

## 🗄️ **Database Overview**

This document provides complete information for accessing and managing the Google Cloud SQL MySQL database for the SecureBank Hub fraud detection application.

### **Database Details:**
- **Instance Name**: `securebank-fraud-db`
- **Database Engine**: MySQL 8.0
- **Region**: us-central1
- **Machine Type**: db-f1-micro
- **Storage**: 10 GB SSD
- **Public IP**: 107.178.210.174
- **Connection Name**: planar-contact-474800-i8:us-central1:securebank-fraud-db

## 🔧 **Database Configuration**

### **Database Settings:**
- **Database Name**: `securebankdb`
- **Username**: `securebank`
- **Password**: `Welcome2025!`
- **Host**: `%` (allows connections from anywhere)
- **Port**: `3306` (default MySQL port)

### **Connection String:**
```
jdbc:mysql://107.178.210.174:3306/securebankdb
```

## 🚀 **Accessing the Database**

### **Method 1: Google Cloud Console (Web Interface)**

#### **Step 1: Navigate to Cloud SQL**
1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Navigate to **"SQL"** under **"Databases"** in the left sidebar
3. Or go directly to: `https://console.cloud.google.com/sql`

#### **Step 2: Select Your Instance**
1. Click on **"securebank-fraud-db"** from the instances list
2. You'll see the overview page with connection details

#### **Step 3: Access Database**
1. **For SQL Queries**: Click **"Cloud SQL Studio"** in the left sidebar
2. **For User Management**: Click **"Users"** in the left sidebar
3. **For Database Management**: Click **"Databases"** in the left sidebar

### **Method 2: Spring Boot Application**

#### **Local Development (H2):**
```bash
./mvnw spring-boot:run
# Uses H2 in-memory database
```

#### **Production (Google Cloud SQL):**
```bash
export DB_HOST=107.178.210.174
export DB_NAME=securebankdb
export DB_USER=securebank
export DB_PASSWORD=Welcome2025!

./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### **Method 3: Direct MySQL Connection**

#### **Using MySQL Client:**
```bash
mysql -h 107.178.210.174 -u securebank -p securebankdb
# Enter password: Welcome2025!
```

#### **Using MySQL Workbench:**
1. **Connection Name**: SecureBank Fraud DB
2. **Hostname**: 107.178.210.174
3. **Port**: 3306
4. **Username**: securebank
5. **Password**: Welcome2025!
6. **Default Schema**: securebankdb

## 📊 **Database Schema**

### **Tables Created:**

#### **1. Users Table**
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    date_of_birth TIMESTAMP(6),
    role ENUM('CUSTOMER', 'ADMIN', 'BANKER') NOT NULL DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);
```

#### **2. Accounts Table**
```sql
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    account_type ENUM('CHECKING', 'SAVINGS', 'BUSINESS') NOT NULL DEFAULT 'CHECKING',
    balance DECIMAL(19,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### **3. Transactions Table (Fraud Detection)**
```sql
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_OUT', 'TRANSFER_IN', 'PURCHASE', 'REFUND', 'FEE') NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    description VARCHAR(255),
    merchant_name VARCHAR(255),
    merchant_category VARCHAR(255),
    location VARCHAR(255),
    ip_address VARCHAR(255),
    device_fingerprint VARCHAR(255),
    fraud_status ENUM('PENDING', 'APPROVED', 'FLAGGED', 'BLOCKED', 'UNDER_REVIEW') DEFAULT 'PENDING',
    fraud_score FLOAT DEFAULT 0.0,
    fraud_reasons VARCHAR(255),
    transaction_timestamp TIMESTAMP(6),
    created_at TIMESTAMP(6),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
```

## 🔍 **Fraud Detection Queries**

### **High-Risk Transactions:**
```sql
SELECT * FROM transactions WHERE fraud_score > 0.7;
```

### **Blocked Transactions:**
```sql
SELECT * FROM transactions WHERE fraud_status = 'BLOCKED';
```

### **Transactions by Location:**
```sql
SELECT location, COUNT(*) as transaction_count, AVG(fraud_score) as avg_fraud_score
FROM transactions 
GROUP BY location;
```

### **Recent Transactions:**
```sql
SELECT * FROM transactions 
WHERE transaction_timestamp >= DATE_SUB(NOW(), INTERVAL 24 HOUR)
ORDER BY transaction_timestamp DESC;
```

### **Account Summary with Fraud Analysis:**
```sql
SELECT 
    a.account_number,
    u.username,
    a.balance,
    COUNT(t.id) as transaction_count,
    AVG(t.fraud_score) as avg_fraud_score,
    COUNT(CASE WHEN t.fraud_status = 'BLOCKED' THEN 1 END) as blocked_count
FROM accounts a
JOIN users u ON a.user_id = u.id
LEFT JOIN transactions t ON a.id = t.account_id
GROUP BY a.id, a.account_number, u.username, a.balance;
```

## 🛠️ **Application Configuration**

### **Environment Variables:**
```bash
# Required for production profile
export DB_HOST=107.178.210.174
export DB_NAME=securebankdb
export DB_USER=securebank
export DB_PASSWORD=Welcome2025!
```

### **Spring Boot Profiles:**

#### **Local Development (application.yml):**
```yaml
spring:
  profiles:
    active: local
  datasource:
    url: jdbc:h2:mem:securebankdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
```

#### **Production (application.yml):**
```yaml
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:mysql://${DB_HOST}:3306/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL8Dialect
```

## 🔐 **Security Considerations**

### **Current Security Status:**
- ✅ **Public IP enabled** for development/testing
- ✅ **Strong password** configured
- ✅ **Database user** with limited privileges
- ⚠️ **Firewall rules** may need configuration for production
- ⚠️ **SSL/TLS** should be enabled for production

### **Production Security Recommendations:**
1. **Enable SSL/TLS** connections
2. **Configure firewall rules** to restrict access
3. **Use Cloud SQL Proxy** for secure connections
4. **Implement connection pooling**
5. **Regular security updates**

## 📈 **Monitoring and Maintenance**

### **Google Cloud Console Monitoring:**
1. **CPU Utilization**: Monitor database performance
2. **Storage Usage**: Track database growth
3. **Connection Count**: Monitor active connections
4. **Query Performance**: Use Query Insights

### **Backup Configuration:**
- **Automatic Backups**: Enabled (daily)
- **Point-in-time Recovery**: Available
- **Backup Retention**: 7 days (configurable)

## 🚨 **Troubleshooting**

### **Common Connection Issues:**

#### **1. Connection Timeout:**
```bash
# Check if instance is running
gcloud sql instances describe securebank-fraud-db

# Check firewall rules
gcloud compute firewall-rules list
```

#### **2. Authentication Failed:**
- Verify username: `securebank`
- Verify password: `Welcome2025!`
- Check user permissions in Cloud SQL console

#### **3. Database Not Found:**
- Ensure database `securebankdb` exists
- Check database permissions for user `securebank`

### **Application Connection Issues:**

#### **Environment Variables Not Set:**
```bash
# Verify environment variables
echo $DB_HOST
echo $DB_NAME
echo $DB_USER
echo $DB_PASSWORD
```

#### **Profile Not Active:**
```bash
# Ensure production profile is active
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📞 **Support and Resources**

### **Google Cloud SQL Documentation:**
- [Cloud SQL Documentation](https://cloud.google.com/sql/docs)
- [MySQL Connection Guide](https://cloud.google.com/sql/docs/mysql/connect-overview)
- [Cloud SQL Pricing](https://cloud.google.com/sql/pricing)

### **Project-Specific Resources:**
- **H2 Database Guide**: `readmeH2Access.md`
- **Fraud Detection Database**: `readmeH2FraudDetectionDatabase.md`
- **GitHub Repository**: `https://github.com/Vorobyevalx/CSCI4830AppBE`

## 📝 **Quick Reference Commands**

### **Start Application with Cloud SQL:**
```bash
export DB_HOST=107.178.210.174
export DB_NAME=securebankdb
export DB_USER=securebank
export DB_PASSWORD=Welcome2025!
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### **Test Database Connection:**
```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/users/count
curl http://localhost:8080/api/transactions/count
```

### **Access Cloud SQL Console:**
```
https://console.cloud.google.com/sql/instances/securebank-fraud-db/overview
```

---

**Last Updated**: October 18, 2025  
**Status**: ✅ Production Ready - Google Cloud SQL MySQL 8.0  
**Cost**: ~$7/month (db-f1-micro instance)
