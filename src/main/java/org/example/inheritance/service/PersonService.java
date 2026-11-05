package org.example.inheritance.service;

import lombok.AllArgsConstructor;
import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.dto.response.PersonResponse;
import org.example.inheritance.factory.PersonFactory;
import org.example.inheritance.model.Person;
import org.example.inheritance.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final List<PersonFactory> factories;

    public PersonResponse createPerson(CreatePersonCommand command) {
        Person personEntity = null;

        for (PersonFactory factory: factories) {
            if (factory.supports(command.personType())) {
                personEntity = factory.create(command);
                break;
            }
        }

        if (personEntity == null) {
            throw new IllegalArgumentException("Person type not supported");
        }

        personRepository.save(personEntity);

        return new PersonResponse(personEntity.getId(), personEntity.getName());
    }
}
