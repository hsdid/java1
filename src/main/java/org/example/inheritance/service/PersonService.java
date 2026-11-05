package org.example.inheritance.service;

import lombok.AllArgsConstructor;
import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.dto.response.PersonResponse;
import org.example.inheritance.factory.PersonFactory;
import org.example.inheritance.model.Person;
import org.example.inheritance.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final Map<String, PersonFactory> factories;

    @Transactional(readOnly = true)
    public PersonResponse createPerson(CreatePersonCommand command) {
        PersonFactory personFactory = factories.get(command.personType().toLowerCase() + "Factory");
        Person person = personRepository.saveAndFlush(personFactory.createPerson(command.parameters()));
        return personFactory.toResponse(person);
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> findAll() {
        return personRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    private PersonResponse mapToResponse(Person person) {
        PersonFactory factory = factories.get(person.getClass().getSimpleName().toLowerCase() + "Factory");
        return factory.toResponse(person);
    }
}
