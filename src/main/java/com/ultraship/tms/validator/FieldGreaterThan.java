package com.ultraship.tms.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldGreaterThanValidator.class)
@Documented
public @interface FieldGreaterThan {

    String message() default "{field} must be greater than {compareTo}";

    String field();
    String compareTo();

    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}