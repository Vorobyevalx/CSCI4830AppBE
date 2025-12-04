package com.securebank.hub.service;

import com.securebank.hub.model.FraudStatus;
import com.securebank.hub.model.Transaction;
import com.securebank.hub.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudDetectionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    // Fraud detection thresholds
    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("10000.00");
    private static final int RAPID_TRANSACTION_COUNT = 5;
    private static final int RAPID_TRANSACTION_MINUTES = 5;
    private static final double HIGH_RISK_SCORE = 0.7;
    private static final double MEDIUM_RISK_SCORE = 0.4;
    
    /**
     * Analyzes a transaction for fraud indicators
     * Returns the fraud score and updates the transaction with fraud status
     */
    @Transactional
    public Transaction analyzeTransaction(Transaction transaction) {
        List<String> fraudReasons = new ArrayList<>();
        double fraudScore = 0.0;
        
        // Rule 1: High amount transaction
        if (isHighAmountTransaction(transaction)) {
            fraudScore += 0.3;
            fraudReasons.add("HIGH_AMOUNT");
        }
        
        // Rule 2: Rapid succession transactions
        if (isRapidSuccession(transaction)) {
            fraudScore += 0.4;
            fraudReasons.add("RAPID_SUCCESSION");
        }
        
        // Rule 3: Unusual location (if location data available)
        if (isUnusualLocation(transaction)) {
            fraudScore += 0.2;
            fraudReasons.add("UNUSUAL_LOCATION");
        }
        
        // Rule 4: Large withdrawal from account with low balance
        if (isLargeWithdrawalFromLowBalance(transaction)) {
            fraudScore += 0.3;
            fraudReasons.add("LARGE_WITHDRAWAL_LOW_BALANCE");
        }
        
        // Rule 5: Multiple transactions from same IP in short time
        if (isSuspiciousIPActivity(transaction)) {
            fraudScore += 0.2;
            fraudReasons.add("SUSPICIOUS_IP_ACTIVITY");
        }
        
        // Cap fraud score at 1.0
        fraudScore = Math.min(fraudScore, 1.0);
        
        // Set fraud status based on score
        FraudStatus fraudStatus;
        if (fraudScore >= HIGH_RISK_SCORE) {
            fraudStatus = FraudStatus.FLAGGED;
        } else if (fraudScore >= MEDIUM_RISK_SCORE) {
            fraudStatus = FraudStatus.UNDER_REVIEW;
        } else {
            fraudStatus = FraudStatus.PENDING;
        }
        
        // Update transaction with fraud analysis
        transaction.setFraudScore(fraudScore);
        transaction.setFraudStatus(fraudStatus);
        if (!fraudReasons.isEmpty()) {
            transaction.setFraudReasons(String.join(", ", fraudReasons));
        }
        
        return transaction;
    }
    
    /**
     * Check if transaction amount exceeds high threshold
     */
    private boolean isHighAmountTransaction(Transaction transaction) {
        return transaction.getAmount() != null && 
               transaction.getAmount().compareTo(HIGH_AMOUNT_THRESHOLD) > 0;
    }
    
    /**
     * Check if there are too many transactions in a short time period
     */
    private boolean isRapidSuccession(Transaction transaction) {
        if (transaction.getAccount() == null || transaction.getAccount().getId() == null) {
            return false;
        }
        
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(RAPID_TRANSACTION_MINUTES);
        List<Transaction> recentTransactions = transactionRepository
                .findRecentTransactionsByAccount(transaction.getAccount().getId(), fiveMinutesAgo);
        
        return recentTransactions.size() >= RAPID_TRANSACTION_COUNT;
    }
    
    /**
     * Check if transaction location is unusual (basic check)
     * Future enhancement: Compare with user's typical locations
     */
    private boolean isUnusualLocation(Transaction transaction) {
        // Basic check: if location is "unknown" or null, it's suspicious
        String location = transaction.getLocation();
        return location == null || location.equalsIgnoreCase("unknown") || location.trim().isEmpty();
    }
    
    /**
     * Check if large withdrawal from account with low balance
     */
    private boolean isLargeWithdrawalFromLowBalance(Transaction transaction) {
        if (transaction.getAccount() == null) {
            return false;
        }
        
        // Check if it's a withdrawal
        boolean isWithdrawal = transaction.getTransactionType() != null &&
                (transaction.getTransactionType().name().contains("WITHDRAWAL") ||
                 transaction.getTransactionType().name().contains("TRANSFER_OUT"));
        
        if (!isWithdrawal) {
            return false;
        }
        
        // Check if amount is significant compared to balance
        BigDecimal balance = transaction.getAccount().getBalance();
        if (balance == null || balance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        BigDecimal amount = transaction.getAmount();
        if (amount == null) {
            return false;
        }
        
        // If withdrawal is more than 50% of balance, it's suspicious
        BigDecimal fiftyPercentOfBalance = balance.multiply(new BigDecimal("0.5"));
        return amount.compareTo(fiftyPercentOfBalance) > 0;
    }
    
    /**
     * Check for suspicious IP activity
     */
    private boolean isSuspiciousIPActivity(Transaction transaction) {
        if (transaction.getIpAddress() == null || transaction.getIpAddress().trim().isEmpty()) {
            return false;
        }
        
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Transaction> recentTransactions = transactionRepository
                .findRecentTransactionsByAccount(
                    transaction.getAccount() != null ? transaction.getAccount().getId() : null,
                    oneHourAgo
                );
        
        // Count transactions from same IP in last hour
        long sameIPCount = recentTransactions.stream()
                .filter(t -> transaction.getIpAddress().equals(t.getIpAddress()))
                .count();
        
        // If more than 3 transactions from same IP in an hour, suspicious
        return sameIPCount > 3;
    }
    
    /**
     * Auto-approve transaction if fraud score is low
     */
    public void autoApproveIfSafe(Transaction transaction) {
        if (transaction.getFraudScore() != null && 
            transaction.getFraudScore() < MEDIUM_RISK_SCORE &&
            transaction.getFraudStatus() == FraudStatus.PENDING) {
            transaction.setFraudStatus(FraudStatus.APPROVED);
        }
    }
}

