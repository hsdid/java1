package org.example.inheritance.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.example.inheritance.factory.PersonFactory;

import java.util.Map;

@AllArgsConstructor
public class PersonTypeValidator implements ConstraintValidator<PersonType, String> {
    private final Map<String, PersonFactory> factories;


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return factories.containsKey(value.toLowerCase() + "Factory");
    }
}
