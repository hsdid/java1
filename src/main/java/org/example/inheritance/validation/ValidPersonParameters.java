package org.example.inheritance.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ValidPersonParametersValidator.class)
@Documented
public @interface ValidPersonParameters {
    String message() default "Invalid parameters for given person type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
