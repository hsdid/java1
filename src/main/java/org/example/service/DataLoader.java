package org.example.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("no-liquibase")
@RequiredArgsConstructor
public class DataLoader {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

//    @PostConstruct
//    public void init() {
//        Author a1 = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));
//        Author a2 = authorRepository.saveAndFlush(new Author("Ewelina", "Damianska", 1900, 2000));
//        bookRepository.saveAndFlush(new Book("Title1", "LEKTURA", true, a1));
//        bookRepository.saveAndFlush(new Book("Title2", "LEKTURA", true, a2));
//        bookRepository.saveAndFlush(new Book("Title3", "LEKTURA", true, a2));
//    }
}
