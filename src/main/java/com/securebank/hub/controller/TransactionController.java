package com.securebank.hub.controller;

import com.securebank.hub.model.Account;
import com.securebank.hub.model.FraudStatus;
import com.securebank.hub.model.Transaction;
import com.securebank.hub.model.TransactionType;
import com.securebank.hub.repository.AccountRepository;
import com.securebank.hub.repository.TransactionRepository;
import com.securebank.hub.service.AccountService;
import com.securebank.hub.service.FraudDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private FraudDetectionService fraudDetectionService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @GetMapping
    public ResponseEntity<?> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long accountId
    ) {
        // Create pageable with sorting by transaction timestamp (newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionTimestamp").descending());
        
        Page<Transaction> transactions;
        
        // Apply filters - prioritize most specific combinations
        if (accountId != null && startDate != null && endDate != null) {
            // Filter by account and date range
            transactions = transactionRepository.findByAccountIdAndTransactionTimestampBetween(
                accountId, startDate, endDate, pageable);
        } else if (accountId != null) {
            // Filter by account only
            transactions = transactionRepository.findByAccountId(accountId, pageable);
        } else if (startDate != null && endDate != null) {
            // Filter by date range only
            transactions = transactionRepository.findByTransactionTimestampBetween(
                startDate, endDate, pageable);
        } else if (type != null) {
            // Filter by transaction type
            transactions = transactionRepository.findByTransactionType(type, pageable);
        } else {
            // No filters - return all with pagination
            transactions = transactionRepository.findAll(pageable);
        }
        
        // Apply type filter in memory if type is provided with other filters
        // (Note: For production, you'd want a custom query method that handles all combinations)
        if (type != null && (accountId != null || (startDate != null && endDate != null))) {
            List<Transaction> filtered = transactions.getContent().stream()
                .filter(t -> t.getTransactionType() == type)
                .toList();
            // Return as list (loses pagination metadata, but works for now)
            return ResponseEntity.ok(filtered);
        }
        
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/account/{accountId}")
    public List<Transaction> getTransactionsByAccount(@PathVariable Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
    
    @GetMapping("/fraud/{status}")
    public List<Transaction> getTransactionsByFraudStatus(@PathVariable FraudStatus status) {
        return transactionRepository.findFraudulentTransactions(status);
    }
    
    @GetMapping("/high-risk/{threshold}")
    public List<Transaction> getHighRiskTransactions(@PathVariable Double threshold) {
        return transactionRepository.findHighRiskTransactions(threshold);
    }
    
    @GetMapping("/recent/{accountId}")
    public List<Transaction> getRecentTransactions(@PathVariable Long accountId, 
                                                  @RequestParam(defaultValue = "24") int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return transactionRepository.findRecentTransactionsByAccount(accountId, since);
    }
    
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
        try {
            // Validate account exists and load it from database
            if (transaction.getAccount() == null || transaction.getAccount().getId() == null) {
                return ResponseEntity.badRequest().body("{\"error\": \"Account is required\"}");
            }
            
            // Load account from database (important for foreign key constraint)
            Optional<Account> accountOpt = accountRepository.findById(transaction.getAccount().getId());
            if (accountOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Account not found\"}");
            }
            
            // Set the loaded account (managed entity)
            transaction.setAccount(accountOpt.get());
            
            // Save transaction first (so fraud detection can query it)
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            // Run fraud detection analysis (after saving)
            savedTransaction = fraudDetectionService.analyzeTransaction(savedTransaction);
            
            // Update account balance
            accountService.updateBalance(savedTransaction);
            
            // Auto-approve if safe
            fraudDetectionService.autoApproveIfSafe(savedTransaction);
            
            // Save again with fraud analysis results
            savedTransaction = transactionRepository.save(savedTransaction);
            
            return ResponseEntity.ok(savedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    @PutMapping("/{id}/fraud-status")
    public ResponseEntity<Transaction> updateFraudStatus(@PathVariable Long id, 
                                                        @RequestParam FraudStatus status,
                                                        @RequestParam(required = false) String reasons) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Transaction transaction = transactionOpt.get();
        transaction.setFraudStatus(status);
        if (reasons != null) {
            transaction.setFraudReasons(reasons);
        }
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return ResponseEntity.ok(updatedTransaction);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getTransactionCount() {
        long count = transactionRepository.count();
        return ResponseEntity.ok(count);
    }
}
