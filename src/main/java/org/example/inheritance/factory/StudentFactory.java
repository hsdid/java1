package org.example.inheritance.factory;

import org.example.dto.response.ErrorDetail;
import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.model.Person;
import org.example.inheritance.model.PersonParameter;
import org.example.inheritance.model.Student;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StudentFactory implements PersonFactory {
    @Override
    public Person create(CreatePersonCommand command) {
        Student student = new Student();

        for (PersonParameter parameter : command.parameters()) {
            if (parameter.name().equals("name")) {
                student.setName(parameter.value());
            }
            if (parameter.name().equals("age")) {
                student.setAge(Integer.parseInt(parameter.value()));
            }
            if (parameter.name().equals("group")) {
                student.setGroup(parameter.value());
            }
            if (parameter.name().equals("scholarship")) {
                student.setScholarship(Integer.parseInt(parameter.value()));
            }
        }

        return student;
    }

    @Override
    public List<String> validate(CreatePersonCommand command) {
        Set<String> names = command.parameters()
                .stream()
                .map(PersonParameter::name)
                .collect(Collectors.toSet());

        List<String> errors = new ArrayList<>();

        if (!names.contains("name")) {
            errors.add("Missing name for student");
        }
        if (!names.contains("age")) {
            errors.add("Missing age for student");
        }
        if (!names.contains("group")) {
            errors.add("Missing group for student");
        }
        if (!names.contains("scholarship")) {
            errors.add("Missing scholarship for student");
        }

        for (PersonParameter p : command.parameters()) {
            if ("scholarship".equals(p.name()) && !isNumeric(p.value())) {
                errors.add("Scholarship must be a number");
            }
            if ("age".equals(p.name()) && !isNumeric(p.value())) {
                errors.add("Age must be a number");
            }
        }

        return errors;
    }

    private boolean isNumeric(String value) {
        if (value == null) return false;
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean supports(String type) {
        return "student".equalsIgnoreCase(type);
    }
}
