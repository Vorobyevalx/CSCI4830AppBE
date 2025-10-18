package com.securebank.hub.controller;

import com.securebank.hub.model.Account;
import com.securebank.hub.model.User;
import com.securebank.hub.repository.AccountRepository;
import com.securebank.hub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findById(id);
        return account.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public List<Account> getAccountsByUser(@PathVariable Long userId) {
        return accountRepository.findByUserId(userId);
    }
    
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        return account.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        // Validate user exists
        if (account.getUser() == null || account.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<User> user = userRepository.findById(account.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        account.setUser(user.get());
        Account savedAccount = accountRepository.save(account);
        return ResponseEntity.ok(savedAccount);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getAccountCount() {
        long count = accountRepository.count();
        return ResponseEntity.ok(count);
    }
}
