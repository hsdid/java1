package org.example.inheritance.factory;

import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.model.Person;

import java.util.List;

public interface PersonFactory {
    Person create(CreatePersonCommand command);
    List<String> validate(CreatePersonCommand command);
    boolean supports(String type);
}
