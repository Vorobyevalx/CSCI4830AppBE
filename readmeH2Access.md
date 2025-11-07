# H2 Database Console Access Guide

## 🗄️ **How to Access H2 Database Console**

### **Step 1: Start the Application**
```bash
./mvnw spring-boot:run
```

### **Step 2: Open H2 Console in Browser**
```
http://localhost:8080/h2-console
```

### **Step 3: Database Connection Settings**
When the H2 console opens, use these settings:

```
JDBC URL: jdbc:h2:mem:securebankdb
User Name: sa
Password: (leave empty)
```

### **Step 4: Click "Connect"**

## 🔧 **Security Configuration Required**

To access the H2 console, the following security configuration is needed in `SecurityConfig.java`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/health", "/api/users/**", "/h2-console/**").permitAll()
            .anyRequest().authenticated()
        )
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions().disable());
    
    return http.build();
}
```

**Important Notes:**
- `/h2-console/**` must be in the permitAll() list
- `frameOptions().disable()` is required for H2 console to work
- CSRF must be disabled for H2 console functionality

## 🧪 **Useful SQL Queries**

### **View All Users:**
```sql
SELECT * FROM USERS;
```

### **View User Details:**
```sql
SELECT ID, USERNAME, EMAIL, FIRST_NAME, LAST_NAME, ROLE, CREATED_AT 
FROM USERS;
```

### **Count Users:**
```sql
SELECT COUNT(*) FROM USERS;
```

### **Create New User:**
```sql
INSERT INTO USERS (USERNAME, EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, ROLE, IS_ACTIVE, CREATED_AT, UPDATED_AT)
VALUES ('admin', 'admin@securebank.com', 'admin123', 'Admin', 'User', 'ADMIN', true, NOW(), NOW());
```

### **Update User:**
```sql
UPDATE USERS SET PHONE_NUMBER = '555-1234' WHERE USERNAME = 'testuser';
```

### **Delete User:**
```sql
DELETE FROM USERS WHERE USERNAME = 'testuser';
```

## 📊 **Database Information**

- **Database Type**: H2 In-Memory Database
- **Profile**: `local` (as defined in application.yml)
- **Table**: `USERS` (auto-created by Hibernate)
- **Connection Pool**: HikariCP
- **Console Path**: `/h2-console`

## 🚨 **Troubleshooting**

### **403 Forbidden Error:**
- Ensure `/h2-console/**` is in the security permitAll() list
- Check that `frameOptions().disable()` is configured
- Restart the application after security changes

### **Connection Failed:**
- Verify the JDBC URL: `jdbc:h2:mem:securebankdb`
- Check that the application is running on port 8080
- Ensure the `local` profile is active

### **Port Already in Use:**
```bash
# Kill process using port 8080
lsof -ti:8080 | xargs kill -9
```

## 🎯 **Current Application Status**

- ✅ **Application**: Running on port 8080
- ✅ **H2 Console**: Accessible at `/h2-console`
- ✅ **Security**: Configured to allow H2 console access
- ✅ **Database**: Users table created automatically
- ✅ **API Endpoints**: `/api/health`, `/api/users/**` working

## 📝 **Notes**

- H2 console is only available in development (local profile)
- Data is stored in memory and will be lost when application stops
- For persistent data, use the `local-file` profile
- For production, use Google Cloud SQL with the `prod` profile
