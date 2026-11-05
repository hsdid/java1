package org.example.inheritance.factory;

import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.dto.response.EmployeeResponse;
import org.example.inheritance.model.Employee;
import org.example.inheritance.model.PersonParameter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component("employeeFactory")
public class EmployeeFactory implements PersonFactory<Employee, EmployeeResponse> {

//    @Override
//    public Person create(CreatePersonCommand command) {
//        Employee employee = new Employee();
//
//        for (PersonParameter parameter : command.parameters()) {
//            if (parameter.name().equals("salary")) {
//                employee.setSalary(Integer.parseInt(parameter.value()));
//            }
//            if (parameter.name().equals("position")) {
//                employee.setPosition(parameter.value());
//            }
//            if (parameter.name().equals("name")) {
//                employee.setName(parameter.value());
//            }
//            if (parameter.name().equals("age")) {
//                employee.setAge(Integer.parseInt(parameter.value()));
//            }
//        }
//
//
//        return employee;
//    }


    @Override
    public Employee createPersonInternal(Map<String, String> parameters) {
        Employee employee = new Employee();
        employee.setSalary(Integer.parseInt(parameters.get("salary")));
        employee.setAge(Integer.parseInt(parameters.get("age")));
        employee.setName(parameters.get("name"));
        employee.setPosition(parameters.get("position"));
        return employee;
    }

    @Override
    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(employee.getId(),
                employee.getName(),
                employee.getAge(),
                employee.getPosition(),
                employee.getSalary());
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

}
