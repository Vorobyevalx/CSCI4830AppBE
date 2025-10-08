# Day 2 Items - SecureBank Backend Enhancements
**Date:** Day 2 of Development  
**Purpose:** Course alignment improvements and best practices from Lec01-Lec06

---

## 📚 Course Alignment Reference

### Technology Context
- **Your Stack**: Spring Boot (Java) + MySQL + Google Cloud E2
- **Course Coverage**: Django (Python) + SQL + AWS/GCP
- **Both are valid**: Different implementations, same SE principles

---

## 1. Django vs Spring Boot Equivalents

### Understanding the Course Examples in Your Context

| Django Concept (Lec03) | Spring Boot Equivalent | Your Implementation |
|------------------------|------------------------|---------------------|
| `views.py` | `@RestController` classes | `AuthController.java`, `TransactionController.java` |
| Django Models | JPA `@Entity` classes | `User.java`, `Transaction.java`, `FraudAlert.java` |
| Django Templates | Thymeleaf/React/JSP | React (frontend team) |
| `settings.py` | `application.yml` | Already configured |
| Django ORM | Spring Data JPA | `UserRepository`, `TransactionRepository` |
| `manage.py migrate` | Flyway/Liquibase | **ADD THIS** (see below) |

### Why This Matters
The course uses Django to teach web development concepts. You're applying the same concepts in Spring Boot. When reviewing course materials:
- Django `@login_required` decorator → Spring `@PreAuthorize` annotation
- Django forms → Spring `@Valid` with DTO classes
- Django middleware → Spring `Filter` and `Interceptor`

---

## 2. Database Migration Strategy (Missing from Original Gameplan)

### Add Flyway for Version-Controlled Migrations

**Based on Lec03 Django migrations concept:**

#### Step 1: Add Flyway Dependency to `pom.xml`
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

#### Step 2: Create Migration Files
```
src/main/resources/db/migration/
├── V1__create_users_table.sql
├── V2__create_accounts_table.sql
├── V3__create_transactions_table.sql
└── V4__create_fraud_alerts_table.sql
```

#### Step 3: Example Migration (V1__create_users_table.sql)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);
```

#### Step 4: Configure `application.yml`
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
  jpa:
    hibernate:
      ddl-auto: validate  # Changed from create-drop
```

### Why This Matters (from Lec03)
- ✅ Version control for database schema (like Django migrations)
- ✅ Consistent dev/prod environments
- ✅ Rollback capability
- ✅ Team collaboration on schema changes

---

## 3. Project Planning Enhancement (Lec06)

### Agile Story Point Estimation

**Your current gameplan has tasks but no effort estimation. Add this:**

| Task | Story Points | Rationale |
|------|--------------|-----------|
| T1: Database Design | 8 | Complex ER modeling + normalization |
| T2: JPA Setup | 3 | Configuration-heavy, low complexity |
| T3: Entity Models | 5 | 4 entities with relationships |
| T4: JWT Authentication | 13 | High complexity, critical security |
| T5: Account APIs | 5 | Standard CRUD operations |
| T6: Transaction APIs | 8 | Complex validation + fraud integration |
| T7: Settings APIs | 3 | Simple update operations |
| T9: Security Hardening | 5 | Multiple security measures |
| T10: Fraud Detection | 13 | Complex algorithm design |
| T12: Integration Testing | 8 | Comprehensive test coverage |

**Total Velocity**: 71 points over 7 weeks  
**Average**: ~10 points/week (realistic for 2-person backend team)

### Planning Poker Session (Week 1, Day 1)
- [ ] Review all tasks with Gabe
- [ ] Use Fibonacci sequence (1,2,3,5,8,13)
- [ ] Discuss assumptions and risks
- [ ] Adjust estimates based on team consensus

---

## 4. Risk Management (Lec06)

### Identified Risks & Mitigation Strategies

#### Risk 1: JWT Implementation Complexity
- **Probability**: Medium | **Impact**: High
- **Mitigation**: 
  - Start T4 in Week 1 (not Week 2)
  - Use Spring Security starter templates
  - Reference: [Spring Security JWT Tutorial](https://spring.io/guides/gs/securing-web/)

#### Risk 2: Cloud SQL Connection Issues
- **Probability**: Medium | **Impact**: High  
- **Mitigation**:
  - Test VPC networking in Week 2
  - Configure Cloud SQL proxy for local testing
  - Keep H2 fallback for emergencies

#### Risk 3: Fraud Detection Algorithm Accuracy
- **Probability**: Low | **Impact**: Medium
- **Mitigation**:
  - Start with simple rule-based approach
  - Document assumptions (e.g., threshold values)
  - Plan for ML enhancement in future sprints

#### Risk 4: Frontend Integration Delays
- **Probability**: High | **Impact**: Medium
- **Mitigation**:
  - Complete API documentation (Swagger) by Week 3
  - Provide Postman collection early
  - Create mock endpoints if needed

---

## 5. Security Enhancements (Lec01)

### Critical Security Checklist

#### Serialization Security (Lec01 Example)
**Problem**: Sensitive data exposure through Java serialization

**Solution**: Prevent serialization of sensitive classes
```java
// In User.java, Account.java
private final void writeObject(ObjectOutputStream out) 
    throws IOException {
    throw new IOException("Object cannot be serialized");
}

private final void readObject(ObjectInputStream in) 
    throws IOException {
    throw new IOException("Object cannot be deserialized");
}
```

#### Cryptography Best Practices (Lec01)
**Problem**: Weak encryption can be exploited

**Solution**: Use strong algorithms
```java
// WRONG (from Lec01)
Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

// CORRECT
Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
SecureRandom random = new SecureRandom();
byte[] iv = new byte[16];
random.nextBytes(iv);
IvParameterSpec ivSpec = new IvParameterSpec(iv);
```

#### API Security Configuration
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf().csrfTokenRepository(
                CookieCsrfTokenRepository.withHttpOnlyFalse()
            )
            .and()
            .headers()
                .contentSecurityPolicy("default-src 'self'")
                .and()
                .frameOptions().deny()
            .and()
            .cors().configurationSource(corsConfigurationSource());
        
        return http.build();
    }
}
```

---

## 6. Code Complexity Management (Lec01)

### Cyclomatic Complexity Targets

**From Lec01**: Keep methods simple, target CC ≤ 10

#### High-Risk Areas to Monitor
1. **FraudDetectionService.analyzeTransaction()**
   - Expected CC: 6-8 (multiple decision points)
   - Mitigation: Break into smaller methods
   
2. **TransactionController validation logic**
   - Expected CC: 4-6 
   - Mitigation: Use Spring Validator classes

#### Example: Reducing Complexity
```java
// BAD: High cyclomatic complexity (CC = 8)
public FraudAlert analyzeTransaction(Transaction tx) {
    if (tx.getAmount() > 10000) {
        if (isRapidSuccession(tx.getAccountId())) {
            if (isUnusualLocation(tx)) {
                // ... nested logic
            }
        }
    }
    // More nested ifs...
}

// GOOD: Lower complexity (CC = 3)
public FraudAlert analyzeTransaction(Transaction tx) {
    if (isHighRiskAmount(tx)) return createAlert(tx, "HIGH_AMOUNT");
    if (isRapidSuccession(tx)) return createAlert(tx, "RAPID_TX");
    if (isUnusualLocation(tx)) return createAlert(tx, "LOCATION");
    return null;
}

private boolean isHighRiskAmount(Transaction tx) {
    return tx.getAmount() > 10000;
}
```

---

## 7. Testing Strategy Enhancement

### Coverage Goals (Lec01)
- **Unit Tests**: 80% coverage minimum
- **Integration Tests**: All critical paths
- **Security Tests**: OWASP Top 10 vulnerabilities

#### Complexity-Driven Testing
```java
@SpringBootTest
class FraudDetectionServiceTest {
    
    @Test
    void shouldDetectHighAmountFraud() {
        // Test path 1: amount > threshold
        Transaction tx = new Transaction();
        tx.setAmount(15000);
        
        FraudAlert alert = fraudService.analyzeTransaction(tx);
        
        assertNotNull(alert);
        assertEquals("HIGH_AMOUNT", alert.getSeverity());
    }
    
    @Test
    void shouldDetectRapidSuccessionFraud() {
        // Test path 2: rapid transactions
        // CC = 2 requires 2 test cases minimum
    }
    
    // Test all independent paths based on CC
}
```

---

## 8. Batch Processing Consideration (Lec01)

### Optional Enhancement for Fraud Detection

**From Lec01**: Batch processing for large-scale analysis

#### Nightly Fraud Analysis Job
```java
@Component
public class FraudBatchAnalyzer {
    
    @Scheduled(cron = "0 0 2 * * ?") // 2 AM daily
    public void analyzeDailyPatterns() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        List<Transaction> transactions = 
            transactionRepo.findByDateBetween(
                yesterday.atStartOfDay(),
                yesterday.atTime(23, 59, 59)
            );
        
        // Batch analysis logic
        Map<Long, RiskScore> accountRisks = 
            calculateRiskScores(transactions);
        
        // Update fraud alerts
        saveBatchResults(accountRisks);
    }
}
```

#### Shell Script Alternative (Lec01 style)
```bash
#!/bin/bash
# fraud-batch-analyzer.sh

YESTERDAY=$(date -d yesterday +%Y-%m-%d)

java -jar fraud-analyzer.jar \
  --mode=batch \
  --date=$YESTERDAY \
  --output=/var/log/fraud/batch-$YESTERDAY.log

# Email results to security team
if [ $? -eq 0 ]; then
    echo "Batch analysis complete" | mail -s "Fraud Report" security@securebank.com
fi
```

---

## 9. Deployment Enhancements

### Environment-Specific Configurations

#### Development Profile (`application-dev.yml`)
```yaml
spring:
  profiles: dev
  datasource:
    url: jdbc:h2:mem:securebankdb
  jpa:
    show-sql: true
  logging:
    level:
      com.securebank: DEBUG
```

#### Production Profile (`application-prod.yml`)
```yaml
spring:
  profiles: prod
  datasource:
    url: jdbc:mysql://${CLOUD_SQL_IP}:3306/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false
  logging:
    level:
      com.securebank: INFO
```

### Google Cloud SQL Setup Checklist
- [ ] Create Cloud SQL instance (MySQL 8.0)
- [ ] Configure VPC peering
- [ ] Set up Cloud SQL Proxy for local testing
- [ ] Create database user with limited privileges
- [ ] Enable SSL connections
- [ ] Configure automated backups

---

## 10. Weekly Execution Plan (Revised)

### Week 1: Foundation + Risk Mitigation
**Monday-Tuesday:**
- [x] Spring Boot project setup (Day 1 - DONE)
- [ ] **NEW**: Add Flyway migrations
- [ ] **NEW**: Planning poker session with Gabe

**Wednesday-Friday:**
- [ ] T1: Database design (start early)
- [ ] **T4: JWT auth** (moved up from Week 2 - risk mitigation)
- [ ] T3: Entity models

### Week 2: Core Development + Security
- [ ] Complete T4 (JWT)
- [ ] T2: JPA configuration
- [ ] **NEW**: Security audit (serialization, crypto)
- [ ] Test Cloud SQL connection

### Week 3-4: API Development
- [ ] T5: Account APIs
- [ ] T6: Transaction APIs (high CC - careful design)
- [ ] T7: Settings APIs
- [ ] **NEW**: Swagger documentation

### Week 5-7: Security & Testing
- [ ] T9: Security hardening
- [ ] T10: Fraud detection
- [ ] T12: Comprehensive testing
- [ ] **NEW**: Batch processing setup (optional)

---

## 📋 Action Items for Tomorrow (Day 3)

### Immediate Tasks
1. [ ] Add Flyway dependency to `pom.xml`
2. [ ] Create initial migration files (V1-V4)
3. [ ] Update `application.yml` with Flyway config
4. [ ] Schedule planning poker with Gabe
5. [ ] Document risk mitigation strategies
6. [ ] Add serialization protection to entity classes

### Code Reviews
7. [ ] Review cyclomatic complexity of existing code
8. [ ] Add unit tests for completed components
9. [ ] Verify security configurations

### Team Coordination
10. [ ] Share Swagger API docs plan with frontend team
11. [ ] Confirm Cloud SQL timeline with DevOps
12. [ ] Update project board with story points

---

## 🎓 Course Material Mapping

### Lec01 → Your Implementation
- **Software Costs**: Maintainable code = lower lifetime costs
- **Complexity Metrics**: CC monitoring in IntelliJ/SonarQube
- **Security**: Serialization + crypto best practices applied
- **Batch Processing**: Optional fraud analysis job

### Lec03 → Your Implementation  
- **Django ORM** → Spring Data JPA
- **Django Migrations** → Flyway migrations
- **Cloud Deployment** → Google Cloud E2 + Cloud SQL
- **Template Rendering** → REST API (React handles UI)

### Lec06 → Your Implementation
- **Project Planning** → Story points + risk matrix
- **Estimation** → Planning poker method
- **Scheduling** → Activity bar chart (existing gameplan)
- **Staff Allocation** → Responsibility matrix (existing)

---

## 🚀 Success Metrics

### Technical Goals
- [ ] All API endpoints respond < 200ms (local)
- [ ] Cyclomatic complexity ≤ 10 per method
- [ ] 80%+ code coverage on critical paths
- [ ] Zero critical security vulnerabilities (OWASP)

### Process Goals  
- [ ] All database changes version-controlled (Flyway)
- [ ] Weekly velocity tracking (story points)
- [ ] Risk review every Monday
- [ ] Team sync every morning (15 min standup)

---

**Next Review**: End of Week 2 (reassess story points, adjust risks)  
**Questions?** Ping Aleksey or Gabe on Slack #backend-dev

---

*This document bridges course concepts (Django/Python) with your implementation (Spring Boot/Java). Both approaches teach the same software engineering principles.*