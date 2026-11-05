package org.example.inheritance.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.example.inheritance.factory.PersonFactory;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PersonTypeValidator implements ConstraintValidator<PersonType, String> {
    private final List<PersonFactory> factories;


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Optional<PersonFactory> factoryExist =  factories
                .stream()
                .filter(factory -> factory.supports(value))
                .findFirst();

        return factoryExist.isPresent();
    }
}
