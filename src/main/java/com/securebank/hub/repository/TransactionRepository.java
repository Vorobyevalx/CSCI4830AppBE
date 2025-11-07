package com.securebank.hub.repository;

import com.securebank.hub.model.Account;
import com.securebank.hub.model.FraudStatus;
import com.securebank.hub.model.Transaction;
import com.securebank.hub.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByAccount(Account account);
    
    List<Transaction> findByAccountId(Long accountId);
    
    List<Transaction> findByFraudStatus(FraudStatus fraudStatus);
    
    List<Transaction> findByTransactionType(TransactionType transactionType);
    
    List<Transaction> findByAmountGreaterThan(BigDecimal amount);
    
    List<Transaction> findByTransactionTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    // Fraud Detection Queries
    @Query("SELECT t FROM Transaction t WHERE t.fraudStatus = :status ORDER BY t.transactionTimestamp DESC")
    List<Transaction> findFraudulentTransactions(@Param("status") FraudStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE t.fraudScore > :threshold ORDER BY t.fraudScore DESC")
    List<Transaction> findHighRiskTransactions(@Param("threshold") Double threshold);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.transactionTimestamp >= :since ORDER BY t.transactionTimestamp DESC")
    List<Transaction> findRecentTransactionsByAccount(@Param("accountId") Long accountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT t FROM Transaction t WHERE t.ipAddress = :ipAddress AND t.transactionTimestamp >= :since")
    List<Transaction> findTransactionsByIpAddress(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
    
    @Query("SELECT t FROM Transaction t WHERE t.location = :location AND t.transactionTimestamp >= :since")
    List<Transaction> findTransactionsByLocation(@Param("location") String location, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.id = :accountId AND t.transactionTimestamp >= :since")
    Long countRecentTransactionsByAccount(@Param("accountId") Long accountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.id = :accountId AND t.transactionTimestamp >= :since AND t.transactionType = :type")
    BigDecimal sumRecentTransactionsByAccountAndType(@Param("accountId") Long accountId, @Param("since") LocalDateTime since, @Param("type") TransactionType type);
}
