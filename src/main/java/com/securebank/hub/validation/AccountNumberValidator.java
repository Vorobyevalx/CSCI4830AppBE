package com.securebank.hub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountNumberValidator implements ConstraintValidator<ValidAccountNumber, String> {
    
    private static final String ACCOUNT_NUMBER_PATTERN = "^[A-Z0-9]{6,20}$";
    
    @Override
    public void initialize(ValidAccountNumber constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String accountNumber, ConstraintValidatorContext context) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return false;
        }
        return accountNumber.matches(ACCOUNT_NUMBER_PATTERN);
    }
}

