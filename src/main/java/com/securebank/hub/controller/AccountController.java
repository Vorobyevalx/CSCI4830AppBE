package com.securebank.hub.controller;

import com.securebank.hub.model.Account;
import com.securebank.hub.model.User;
import com.securebank.hub.repository.AccountRepository;
import com.securebank.hub.repository.UserRepository;
import com.securebank.hub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    
    @Autowired
    private UserService userService;
    
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
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Find user by username from authentication
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        User currentUser = userOpt.get();
        
        // If user ID is provided in request, validate it matches current user (for admin operations)
        if (account.getUser() != null && account.getUser().getId() != null) {
            if (!account.getUser().getId().equals(currentUser.getId())) {
                // Only allow if current user is admin
                if (!currentUser.getRole().name().equals("ADMIN")) {
                    return ResponseEntity.status(403).build();
                }
                // Admin can create account for other users
                Optional<User> targetUser = userRepository.findById(account.getUser().getId());
                if (targetUser.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                account.setUser(targetUser.get());
            } else {
                account.setUser(currentUser);
            }
        } else {
            // No user specified, use current authenticated user
            account.setUser(currentUser);
        }
        
        // Set default values if not provided
        if (account.getAccountType() == null) {
            account.setAccountType(com.securebank.hub.model.AccountType.CHECKING);
        }
        if (account.getBalance() == null) {
            account.setBalance(java.math.BigDecimal.ZERO);
        }
        if (account.getIsActive() == null) {
            account.setIsActive(true);
        }
        
        Account savedAccount = accountRepository.save(account);
        return ResponseEntity.ok(savedAccount);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getAccountCount() {
        long count = accountRepository.count();
        return ResponseEntity.ok(count);
    }
}
