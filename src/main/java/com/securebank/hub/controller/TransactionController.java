package com.securebank.hub.controller;

import com.securebank.hub.exception.ResourceNotFoundException;
import com.securebank.hub.exception.ValidationException;
import com.securebank.hub.model.Account;
import com.securebank.hub.model.FraudStatus;
import com.securebank.hub.model.Transaction;
import com.securebank.hub.model.TransactionType;
import com.securebank.hub.repository.AccountRepository;
import com.securebank.hub.repository.TransactionRepository;
import com.securebank.hub.service.AccountService;
import com.securebank.hub.service.AuditLogService;
import com.securebank.hub.service.FraudDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    
    @Autowired
    private AuditLogService auditLogService;
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.securebank.hub.model.User) {
            return ((com.securebank.hub.model.User) authentication.getPrincipal()).getUsername();
        }
        return "unknown";
    }
    
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
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
        return ResponseEntity.ok(transaction);
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
    @Transactional
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        // Validate account exists and load it from database
        if (transaction.getAccount() == null || transaction.getAccount().getId() == null) {
            throw new ValidationException("Account is required");
        }
        
        // Load account from database (important for foreign key constraint)
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", transaction.getAccount().getId()));
        
        // Set the loaded account (managed entity)
        transaction.setAccount(account);
        
        // Save transaction first (so fraud detection can query it)
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Run fraud detection analysis (after saving)
        savedTransaction = fraudDetectionService.analyzeTransaction(savedTransaction);
        
        // Update account balance (will throw InsufficientFundsException if needed)
        accountService.updateBalance(savedTransaction);
        
        // Auto-approve if safe
        fraudDetectionService.autoApproveIfSafe(savedTransaction);
        
        // Save again with fraud analysis results
        savedTransaction = transactionRepository.save(savedTransaction);
        
        // Audit log transaction creation
        String username = getCurrentUsername();
        auditLogService.logTransactionCreation(
            savedTransaction.getId(),
            savedTransaction.getAccount().getId(),
            savedTransaction.getTransactionType().name(),
            savedTransaction.getAmount().doubleValue(),
            username
        );
        
        return ResponseEntity.ok(savedTransaction);
    }
    
    @PutMapping("/{id}/fraud-status")
    public ResponseEntity<Transaction> updateFraudStatus(@PathVariable Long id, 
                                                        @RequestParam FraudStatus status,
                                                        @RequestParam(required = false) String reasons) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
        
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
