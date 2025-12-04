package com.securebank.hub.service;

import com.securebank.hub.model.Account;
import com.securebank.hub.model.Transaction;
import com.securebank.hub.model.TransactionType;
import com.securebank.hub.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    /**
     * Update account balance based on transaction
     * Returns the updated account
     */
    @Transactional
    public Account updateBalance(Transaction transaction) {
        if (transaction.getAccount() == null || transaction.getAccount().getId() == null) {
            throw new RuntimeException("Transaction must have a valid account");
        }
        
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        BigDecimal amount = transaction.getAmount();
        if (amount == null) {
            throw new RuntimeException("Transaction amount cannot be null");
        }
        
        BigDecimal currentBalance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
        BigDecimal newBalance;
        
        TransactionType type = transaction.getTransactionType();
        
        // Determine if this increases or decreases balance
        switch (type) {
            case DEPOSIT:
            case TRANSFER_IN:
            case REFUND:
                newBalance = currentBalance.add(amount);
                break;
                
            case WITHDRAWAL:
            case TRANSFER_OUT:
            case PURCHASE:
            case FEE:
                // Check for insufficient funds
                if (currentBalance.compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient funds. Current balance: $" + currentBalance + ", Required: $" + amount);
                }
                newBalance = currentBalance.subtract(amount);
                break;
                
            default:
                throw new RuntimeException("Unknown transaction type: " + type);
        }
        
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
    
    /**
     * Get account by ID
     */
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}

