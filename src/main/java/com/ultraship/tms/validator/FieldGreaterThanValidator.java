package com.ultraship.tms.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class FieldGreaterThanValidator
        implements ConstraintValidator<FieldGreaterThan, Object> {

    private String fieldName;
    private String compareToField;

    @Override
    public void initialize(FieldGreaterThan constraintAnnotation) {
        this.fieldName = constraintAnnotation.field();
        this.compareToField = constraintAnnotation.compareTo();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        try {
            Field field = value.getClass().getDeclaredField(fieldName);
            Field compareField = value.getClass().getDeclaredField(compareToField);

            field.setAccessible(true);
            compareField.setAccessible(true);

            BigDecimal fieldValue = (BigDecimal) field.get(value);
            BigDecimal compareValue = (BigDecimal) compareField.get(value);

            if (fieldValue == null || compareValue == null) return true;

            if (fieldValue.compareTo(compareValue) <= 0) {

                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                fieldName + " must be greater than " + compareToField
                        )
                        .addPropertyNode(fieldName)
                        .addConstraintViolation();

                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}