package com.securebank.hub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "Phone number must be in format: +1234567890 or (123) 456-7890 or 123-456-7890";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

