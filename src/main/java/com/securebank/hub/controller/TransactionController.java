package com.securebank.hub.controller;

import com.securebank.hub.model.FraudStatus;
import com.securebank.hub.model.Transaction;
import com.securebank.hub.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
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
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
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
