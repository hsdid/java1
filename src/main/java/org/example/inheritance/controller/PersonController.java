package org.example.inheritance.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.dto.response.PersonResponse;
import org.example.inheritance.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/people")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonResponse>> getAll() {
        return ResponseEntity.ok(personService.findAll());
    }


    @PostMapping
    public ResponseEntity<PersonResponse> addPerson(@Valid @RequestBody CreatePersonCommand person) {
        PersonResponse personResponse = personService.createPerson(person);

        return ResponseEntity.ok(personResponse);
    }
}
