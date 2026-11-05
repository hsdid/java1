package org.example.inheritance.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.factory.PersonFactory;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class ValidPersonParametersValidator implements ConstraintValidator<ValidPersonParameters, CreatePersonCommand> {
    private final Map<String, PersonFactory> factories;

    @Override
    public boolean isValid(CreatePersonCommand command, ConstraintValidatorContext context) {
        if (command == null || command.personType() == null || command.parameters() == null) {
            return true;
        }

        PersonFactory factory = factories.get(command.personType().toLowerCase() + "Factory");
        List<String> errors = factory.validate(command);

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
