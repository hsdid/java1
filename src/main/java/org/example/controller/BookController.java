package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.CreateBookRequest;
import org.example.dto.request.PatchBookRequest;
import org.example.dto.request.UpdateBookRequest;
import org.example.dto.response.BookResponse;
import org.example.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/books")
@Slf4j
//@Scope("session")
public class BookController {
    private final BookService bookService;

    private int counter = 0;

//    @GetMapping("/test")
//    public void test() {
//        log.info("counter: " + counter++);
//    }

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getBooks(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(bookService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookByID(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody CreateBookRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.delete(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBook(
            @Valid @RequestBody UpdateBookRequest request,
            @PathVariable Long id
    ) {
        bookService.update(request, id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void partialUpdateBook(
            @Valid @RequestBody PatchBookRequest request,
            @PathVariable Long id
    ) {
        bookService.partialUpdate(request, id);
    }

}
