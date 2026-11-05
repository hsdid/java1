package org.example.inheritance.controller;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.dto.response.PersonResponse;
import org.example.inheritance.model.Employee;
import org.example.inheritance.model.Student;
import org.example.inheritance.repository.PersonRepository;
import org.example.inheritance.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/people")
@RequiredArgsConstructor
public class PersonController {

    private final PersonRepository personRepository;
    private final PersonService personService;

    @PostConstruct
    public void init() {
        personRepository.saveAllAndFlush(List.of(
                new Employee("Adam", 20, "programmer", 15000),
                new Employee("Eve", 25, "designer", 20000),
                new Employee("John", 30, "manager", 30000),
                new Student("Jan", 20, "1a", 0),
                new Student("Kasia", 21, "3a", 500),
                new Student("Maciek", 19, "1a", 300)
        ));
    }

    @GetMapping
    public ResponseEntity<List<PersonResponse>> getAll() {
        return ResponseEntity.ok(personService.findAll());
    }


    // todo
    // chcemy dodac nowy endpoint w ktorym mozemy dodac dowolną osobe
    // UWAGA; powinna byc taka konstrukcja i taki kontrakt ze dodanie nowego typu osoby nie sprawi ze bede musial modywikowac istniejace klasy
    // tylko stworze nowe, dodajemy serwis

    // na koniec mozesz dodac nowa osobe i zobaczyc czy nie musiz modyfikowac istnijacych klas
    //
    @PostMapping
    public ResponseEntity<PersonResponse> addPerson(@Valid @RequestBody CreatePersonCommand person) {
        PersonResponse personResponse = personService.createPerson(person);

        return ResponseEntity.ok(personResponse);
    }
}
