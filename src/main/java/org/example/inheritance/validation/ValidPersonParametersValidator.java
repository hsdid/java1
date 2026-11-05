package org.example.inheritance.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.response.ErrorDetail;
import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.factory.PersonFactory;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class ValidPersonParametersValidator implements ConstraintValidator<ValidPersonParameters, CreatePersonCommand> {
    private final List<PersonFactory> factories;

    @Override
    public boolean isValid(CreatePersonCommand command, ConstraintValidatorContext context) {
        if (command == null || command.personType() == null || command.parameters() == null) {
            return true;
        }

        List<String> errors = null;

        for (PersonFactory factory : factories) {
            if (factory.supports(command.personType())) {
                errors = factory.validate(command);
                break;
            }
        }


        if (errors != null && !errors.isEmpty()) {
            for (String error : errors) {
                addError(error, context);
            }

            return false;
        }

        return true;
    }

    private void addError(String message, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("parameters")
                .addConstraintViolation();
    }
}
