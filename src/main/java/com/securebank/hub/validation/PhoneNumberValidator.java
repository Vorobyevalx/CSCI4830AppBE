package com.securebank.hub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    
    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        // Phone number is optional, so null/empty is valid
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true;
        }
        // Remove spaces and common formatting characters for validation
        String cleaned = phoneNumber.replaceAll("[\\s()-]", "");
        return cleaned.matches("^\\+?1?[0-9]{10}$");
    }
}

