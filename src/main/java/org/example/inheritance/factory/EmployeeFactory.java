package org.example.inheritance.factory;

import org.example.dto.response.ErrorDetail;
import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.model.Employee;
import org.example.inheritance.model.Person;
import org.example.inheritance.model.PersonParameter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EmployeeFactory implements PersonFactory {
    @Override
    public Person create(CreatePersonCommand command) {
        Employee employee = new Employee();

        for (PersonParameter parameter : command.parameters()) {
            if (parameter.name().equals("salary")) {
                employee.setSalary(Integer.parseInt(parameter.value()));
            }
            if (parameter.name().equals("position")) {
                employee.setPosition(parameter.value());
            }
            if (parameter.name().equals("name")) {
                employee.setName(parameter.value());
            }
            if (parameter.name().equals("age")) {
                employee.setAge(Integer.parseInt(parameter.value()));
            }
        }


        return employee;
    }

    @Override
    public List<String> validate(CreatePersonCommand command) {
        List<String> errors = new ArrayList<>();
        Set<String> names = command.parameters()
                .stream()
                .map(PersonParameter::name)
                .collect(Collectors.toSet());

        if (!names.contains("salary")) {
            errors.add("Missing salary for employee");
        }

        if (!names.contains("position")) {
            errors.add("Missing position for employee");
        }
        if (!names.contains("name")) {
            errors.add("Missing name for employee");
        }
        if (!names.contains("age")) {
            errors.add("Missing age for employee");
        }

        for (PersonParameter p : command.parameters()) {
            if ("salary".equals(p.name()) && !isNumeric(p.value())) {
                errors.add("Salary must be a number");

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
        return "employee".equalsIgnoreCase(type);
    }
}
