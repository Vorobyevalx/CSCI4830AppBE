package com.securebank.hub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AccountNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccountNumber {
    String message() default "Account number must be alphanumeric and between 6-20 characters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

