package com.example.snsbackend.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class NoBlankInListValidator implements ConstraintValidator<NoBlankInList, List<String>> {

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.stream().noneMatch(str -> str == null || str.trim().isEmpty());
    }
}