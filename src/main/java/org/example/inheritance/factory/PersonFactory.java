package org.example.inheritance.factory;

import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.dto.response.PersonResponse;
import org.example.inheritance.model.Person;
import org.example.inheritance.model.PersonParameter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface PersonFactory<ENTITY extends Person, RESPONSE extends PersonResponse> {
    default ENTITY createPerson(List<PersonParameter> parameters) {
        return createPersonInternal(parameters.stream().collect(Collectors.toMap(PersonParameter::name, PersonParameter::value)));
    }

    ENTITY createPersonInternal(Map<String, String> parameters);

    List<String> validate(CreatePersonCommand command);
    RESPONSE toResponse(ENTITY person);
}
