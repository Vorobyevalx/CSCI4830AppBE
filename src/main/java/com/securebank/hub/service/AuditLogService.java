package com.securebank.hub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditLogService {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Log authentication attempt (success or failure)
     */
    public void logAuthenticationAttempt(String username, boolean success, String reason, String ipAddress) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("eventType", "AUTHENTICATION");
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("username", username);
        logData.put("success", success);
        logData.put("ipAddress", ipAddress);
        if (!success && reason != null) {
            logData.put("failureReason", reason);
        }
        
        logJson("AUTHENTICATION", logData);
    }
    
    /**
     * Log user registration
     */
    public void logUserRegistration(String username, String email, boolean success, String reason) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("eventType", "USER_REGISTRATION");
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("username", username);
        logData.put("email", email);
        logData.put("success", success);
        if (!success && reason != null) {
            logData.put("failureReason", reason);
        }
        
        logJson("USER_REGISTRATION", logData);
    }
    
    /**
     * Log transaction creation
     */
    public void logTransactionCreation(Long transactionId, Long accountId, String transactionType, 
                                       Double amount, String username) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("eventType", "TRANSACTION_CREATED");
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("transactionId", transactionId);
        logData.put("accountId", accountId);
        logData.put("transactionType", transactionType);
        logData.put("amount", amount);
        logData.put("username", username);
        
        logJson("TRANSACTION_CREATED", logData);
    }
    
    /**
     * Log fraud detection event
     */
    public void logFraudDetection(Long transactionId, String fraudStatus, Double fraudScore, 
                                  String fraudReasons, String username) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("eventType", "FRAUD_DETECTION");
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("transactionId", transactionId);
        logData.put("fraudStatus", fraudStatus);
        logData.put("fraudScore", fraudScore);
        logData.put("fraudReasons", fraudReasons);
        logData.put("username", username);
        
        logJson("FRAUD_DETECTION", logData);
    }
    
    /**
     * Log token refresh
     */
    public void logTokenRefresh(String username, boolean success, String reason) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("eventType", "TOKEN_REFRESH");
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("username", username);
        logData.put("success", success);
        if (!success && reason != null) {
            logData.put("failureReason", reason);
        }
        
        logJson("TOKEN_REFRESH", logData);
    }
    
    /**
     * Generic method to log JSON structured data
     */
    private void logJson(String eventType, Map<String, Object> data) {
        try {
            String jsonLog = objectMapper.writeValueAsString(data);
            auditLogger.info(jsonLog);
        } catch (JsonProcessingException e) {
            auditLogger.error("Failed to serialize audit log: {}", e.getMessage());
            // Fallback to simple log
            auditLogger.info("AUDIT: {} - {}", eventType, data);
        }
    }
}

