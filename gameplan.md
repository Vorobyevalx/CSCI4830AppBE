# SecureBank Hub - Backend Development Gameplan
**Developer:** Aleksey Vorobyev  
**Timeline:** 7 Weeks  
**Target Deployment:** Google Cloud Compute Engine (E2)

---

## 🚀 Phase 0: Initial Setup (Day 1)

### Step 1: Generate Spring Boot Project
```bash
curl https://start.spring.io/starter.tgz \
  -d dependencies=web,data-jpa,h2,mysql,security,validation,actuator \
  -d bootVersion=3.2.0 \
  -d javaVersion=17 \
  -d type=maven-project \
  -d groupId=com.securebank \
  -d artifactId=securebank-backend \
  -d name=SecureBankHub \
  -d packageName=com.securebank.hub \
  -d packaging=jar \
  | tar -xzvf -

cd securebank-backend
```

### Step 2: Project Structure Setup
Create the following package structure:
```
src/main/java/com/securebank/hub/
├── config/          # Security, CORS, JPA configs
├── controller/      # REST API endpoints
├── model/          # JPA entities
├── repository/     # Data access layer
├── service/        # Business logic
├── security/       # JWT, authentication
├── dto/            # Data transfer objects
└── exception/      # Custom exceptions
```

### Step 3: Add JWT Dependencies to `pom.xml`
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

### Step 4: Configure Application Properties
Create `src/main/resources/application.yml`:
```yaml
spring:
  profiles:
    active: local
  
---
# Local Development Profile
spring:
  config:
    activate:
      on-profile: local
  datasource:
    url: jdbc:h2:mem:securebankdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    
---
# Production Profile (Google Cloud)
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:mysql://${DB_HOST}:3306/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
```

---

## 📅 Week 1-2: M1 - Backend Infrastructure Setup

### ✅ T1: Database Design and Implementation (with Gabe)
**Your Tasks:**

1. **Design Entity Relationship Diagram**
   - Users (id, username, email, password_hash, created_at)
   - Accounts (id, user_id, account_number, balance, account_type)
   - Transactions (id, account_id, amount, type, timestamp, description)
   - FraudAlerts (id, transaction_id, severity, detected_at, status)

2. **Create SQL Schema File**
   - Create `src/main/resources/schema.sql` for production
   - Document all table relationships
   - Add indexes for performance

3. **Validation Checklist:**
   - [ ] Proper normalization (3NF minimum)
   - [ ] Foreign key constraints defined
   - [ ] Indexes on frequently queried columns
   - [ ] Timestamps for audit trails

---

### ✅ T3: Implement Entity Models and Repositories (with Gabe)
**Your Tasks:**

1. **Create Entity Classes** (`src/main/java/com/securebank/hub/model/`)

```java
// User.java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;
    
    // Getters, setters, constructors
}
```

2. **Create Repositories** (`src/main/java/com/securebank/hub/repository/`)
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
}
```

3. **Repeat for:** Account, Transaction, FraudAlert entities

---

### ✅ T4: Develop User Authentication System
**Your Primary Responsibility**

#### Part 1: JWT Utility Class
Create `src/main/java/com/securebank/hub/security/JwtUtil.java`:
```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(UserDetails userDetails) {
        // Implementation
    }
    
    public boolean validateToken(String token) {
        // Implementation
    }
    
    public String extractUsername(String token) {
        // Implementation
    }
}
```

#### Part 2: Security Configuration
Create `src/main/java/com/securebank/hub/config/SecurityConfig.java`:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // Configure authentication, CORS, CSRF
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### Part 3: Authentication Controller
Create `src/main/java/com/securebank/hub/controller/AuthController.java`:
- POST `/api/auth/register` - User registration
- POST `/api/auth/login` - User login (returns JWT)
- POST `/api/auth/refresh` - Token refresh

#### Validation Checklist:
- [ ] Passwords hashed with BCrypt (12 rounds minimum)
- [ ] JWT tokens expire after reasonable time (15 min access, 7 day refresh)
- [ ] Input validation on all endpoints
- [ ] Proper error messages (no information leakage)

---

## 📅 Week 3-4: M2 - Core API Development

### ✅ T6: Build Transaction Management APIs
**Your Primary Responsibility**

#### Endpoints to Create:
1. **GET** `/api/transactions` - Fetch transaction history
   - Query params: `page`, `size`, `startDate`, `endDate`, `type`
   - Implement pagination with Spring Data
   
2. **POST** `/api/transactions` - Create new transaction
   - Validation: amount > 0, valid account
   - Atomic operation with database transaction
   
3. **GET** `/api/transactions/{id}` - Get single transaction

#### Implementation Steps:
```java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @GetMapping
    public Page<TransactionDTO> getTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) LocalDateTime startDate,
        @RequestParam(required = false) LocalDateTime endDate
    ) {
        // Implementation with fraud detection integration
    }
}
```

#### Key Requirements:
- [ ] All operations are atomic (use @Transactional)
- [ ] Comprehensive audit logging
- [ ] Input validation with @Valid
- [ ] Proper HTTP status codes
- [ ] Rate limiting (100 requests/minute per user)

---

## 📅 Week 5-7: M3 - Security, Fraud Detection, and Testing

### ✅ T9: API Security Hardening
**Your Primary Responsibility**

#### Security Measures to Implement:

1. **CORS Configuration**
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000") // Frontend
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}
```

2. **Rate Limiting** (Using Bucket4j)
3. **Input Sanitization** (Prevent SQL Injection)
4. **Security Headers** (X-Frame-Options, CSP, etc.)
5. **HTTPS Enforcement** (for production)

#### Checklist:
- [ ] SQL injection prevention (use JPA properly)
- [ ] XSS protection headers configured
- [ ] CSRF protection for state-changing operations
- [ ] Rate limiting on authentication endpoints
- [ ] Sensitive data not logged

---

### ✅ T10: Develop Fraud Detection Service (with Gabe)
**Your Tasks:**

#### Create Fraud Detection Service
```java
@Service
public class FraudDetectionService {
    
    public FraudAlert analyzeTransaction(Transaction transaction) {
        // Rule-based detection:
        // 1. Amount > $10,000 in single transaction
        // 2. > 5 transactions in 5 minutes
        // 3. Transaction from unusual location (future enhancement)
        // 4. Rapid balance depletion pattern
    }
    
    private boolean isUnusualAmount(Transaction tx) {
        // Compare to user's historical average
    }
    
    private boolean isRapidSuccession(Long accountId) {
        // Check recent transaction frequency
    }
}
```

#### Integration Points:
- Trigger fraud check on every new transaction
- Store alerts in `fraud_alerts` table
- Return alert status in transaction response
- Create endpoint: GET `/api/fraud/alerts` for frontend

---

### ✅ T12: Integration Testing and Bug Fixes (All Team Members)

#### Your Testing Responsibilities:

1. **Unit Tests** (JUnit 5 + Mockito)
```java
@SpringBootTest
class AuthenticationServiceTest {
    @Test
    void shouldRegisterUserSuccessfully() {
        // Test implementation
    }
}
```

2. **Integration Tests** (TestContainers for database)
3. **API Testing** (Postman collection or REST Assured)

#### Test Coverage Goals:
- [ ] Authentication flow (register, login, token validation)
- [ ] Transaction CRUD operations
- [ ] Fraud detection rules
- [ ] Error handling and edge cases
- [ ] Concurrent transaction handling

---

## 🌐 Google Cloud Deployment Preparation

### Step 1: Create Deployment JAR
```bash
./mvnw clean package -DskipTests
```

### Step 2: Dockerfile for GCE2
Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/securebank-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

### Step 3: Environment Variables for Production
```bash
export DB_HOST=<cloud-sql-ip>
export DB_NAME=securebankdb
export DB_USER=<username>
export DB_PASSWORD=<password>
export JWT_SECRET=<generate-secure-secret>
```

### Step 4: Google Cloud Setup Checklist
- [ ] Create GCE2 instance (e2-medium recommended)
- [ ] Set up Cloud SQL (MySQL 8.0)
- [ ] Configure VPC networking
- [ ] Set up Cloud Load Balancer (optional)
- [ ] Configure SSL certificate
- [ ] Set up Cloud Monitoring

---

## 📋 Weekly Checklist Tracker

### Week 1-2: M1 Milestone
- [ ] Day 1-2: Project setup and initial configuration
- [ ] Day 3-4: Database design and schema (T1)
- [ ] Day 5-7: Entity models and repositories (T3)
- [ ] Day 8-10: JWT authentication system (T4)
- [ ] Day 11-14: Testing and refinement

### Week 3-4: M2 Milestone
- [ ] Transaction management APIs (T6)
- [ ] Integration with Gabe's account endpoints (T5, T7)
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Postman collection for frontend team

### Week 5-7: M3 Milestone
- [ ] Security hardening (T9)
- [ ] Fraud detection implementation (T10)
- [ ] Comprehensive testing (T12)
- [ ] Deployment preparation
- [ ] Final demo preparation

---

## 🔧 Useful Commands

### Development
```bash
# Run locally with H2
./mvnw spring-boot:run

# Run with production profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Access H2 console
http://localhost:8080/h2-console

# Run tests
./mvnw test
```

### Build and Deploy
```bash
# Package JAR
./mvnw clean package

# Build Docker image
docker build -t securebank-backend .

# Run Docker container
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod securebank-backend
```

---

## 📚 Key Resources

- **Spring Security Documentation:** https://spring.io/projects/spring-security
- **JWT Best Practices:** https://tools.ietf.org/html/rfc8725
- **Google Cloud SQL:** https://cloud.google.com/sql/docs
- **Spring Data JPA:** https://spring.io/projects/spring-data-jpa

---

## 🚨 Common Pitfalls to Avoid

1. **Don't** hardcode JWT secrets (use environment variables)
2. **Don't** return detailed error messages to clients (info leakage)
3. **Don't** forget to sanitize user inputs
4. **Don't** use `ddl-auto: create-drop` in production
5. **Don't** log sensitive information (passwords, tokens)
6. **Always** use HTTPS in production
7. **Always** implement proper exception handling
8. **Always** write tests before deploying

---

## 💡 Success Metrics

- [ ] All API endpoints respond < 200ms (local)
- [ ] 90%+ code coverage on critical paths
- [ ] Zero critical security vulnerabilities
- [ ] Successful fraud detection on test cases
- [ ] Clean deployment to Google Cloud
- [ ] Frontend team successfully integrated

---

**Good luck, Aleksey! You've got this! 🚀**