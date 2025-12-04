package com.securebank.hub.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    private final BigDecimal currentBalance;
    private final BigDecimal requiredAmount;
    
    public InsufficientFundsException(BigDecimal currentBalance, BigDecimal requiredAmount) {
        super(String.format("Insufficient funds. Current balance: $%.2f, Required: $%.2f", 
            currentBalance, requiredAmount));
        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
    }
    
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    
    public BigDecimal getRequiredAmount() {
        return requiredAmount;
    }
}

