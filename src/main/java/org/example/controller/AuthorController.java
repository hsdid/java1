package org.example.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.dto.request.CreateAuthorRequest;
import org.example.dto.response.AuthorResponse;
import org.example.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
@AllArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/test")
    public void test() throws IOException {
        authorService.playWithTransactions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse createAuthor(
            @Valid @RequestBody CreateAuthorRequest request
    ) {
        return authorService.create(request);
    }

    @GetMapping
    public List<AuthorResponse> getAuthors() {
        return authorService.getAll();
    }

    @GetMapping("/{id}")
    public AuthorResponse getAuthor(@PathVariable Long id) {

        return authorService.getById(id);
    }
}
