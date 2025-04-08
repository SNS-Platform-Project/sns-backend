package com.example.snsbackend.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoBlankInListValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoBlackInList {
    String message() default "List contains blank string(s)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
