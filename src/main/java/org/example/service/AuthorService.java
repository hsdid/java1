package org.example.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.CreateAuthorRequest;
import org.example.dto.response.AuthorResponse;
import org.example.exception.AuthorNotFoundException;
import org.example.model.Author;
import org.example.repository.AuthorRepository;
import org.example.support.AuthorPageableSortTranslator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorPageableSortTranslator sortTranslator;


    public AuthorResponse create(CreateAuthorRequest request) {
        Author savedAuthor = authorRepository.save(new Author(request.firstName(), request.lastName(), request.birthYear(), request.deathYear()));
        return AuthorResponse.from(savedAuthor);
    }

    //Metoda nie rzuca wyjatku LazyInitializationException dzieki transakjcji ale jest n + 1

//    @Transactional(readOnly = true)
//    public List<AuthorResponse> getAll() {
//        return authorRepository.findAll().stream()
//                .map(AuthorResponse::from)
//                .toList();
//    }

    // metoda zwraca authorow z iloscią ksiązek bez n + 1
    @Transactional(readOnly = true)
    public Page<AuthorResponse> getAll(Pageable pageable) {
//        Pageable pageableTranslated = sortTranslator.translate(pageable);
        return authorRepository.findAllWithBooks(pageable);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public AuthorResponse getById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(
                        "Author with id " + id + " not found"
                ));

        return AuthorResponse.from(author);
    }

    /// /    @Transactional
//    public void playWithTransactions() {
//        log.info("-----------------------");
//        Author a1 = authorRepository.findById(1L).get();
//        Author a2 = authorRepository.findById(1L).get();
//        log.info("A1 == A2: " + (a1 == a2));
//        log.info("-----------------------");
//    }
//    @Transactional
//    public void playWithTransactions() {
//        log.info("-----------------------");
//        Author a1 = authorRepository.findById(1L).get();
//        Author a2 = authorRepository.findById(2L).get();
//        a1.setFirstName("zmienione");
//        a2.setFirstName("jakies");
//        authorRepository.saveAndFlush(a1);
//        log.info("-----------------------");
//    }

//    @Transactional
//    public void playWithTransactions() {
//        log.info("-----------------------");
//        Author a1 = authorRepository.findById(2L).get();
//        log.info("{} ma ksiazek: {}", a1.getFirstName(), a1.getBooks().size());
//        log.info("-----------------------");
//    }

//    @Transactional
//    public void playWithTransactions() {
//        log.info("-----------------------");
//        for (Author author : authorRepository.findAllWithBooks()) {
//            log.info("{} ma ksiazek: {}", author.getFirstName(), author.getBooks().size());
//        }
//        log.info("-----------------------");
//    }

//    @Transactional
//    public void playWithTransactions() {
//        log.info("-----------------------");
//        Author a1 = authorRepository.findById(1L).get();
//        a1.setFirstName("zmienione");
//        authorRepository.saveAndFlush(a1);
//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        log.info("-----------------------");
//        throw new IllegalStateException("Wycofanie transakcji");
//    }

    //todo
    // poziomy izolacji
    // checked i unchecked exceptions dla ktorych sie wycofuje dla ktorych nie
    // rollbackFor i noRollbackFor
    // co zrobi SneakyThrows przy Exception
    // spróbuj pobrać wszystkich autorów z iloscią ksiązek, sprobuj, zobacz jakie bledy po kolei wyskakuja itp


    // Checked Exceptions Domyślnie NIE wycofują (nie robią rollbacku)
    // Unchecked Exceptions (niesprawdzane – dziedziczące po RuntimeException): Domyślnie wycofują (robią rollback) transakcję.


    //  Ustawienie unchecked exceptions zeby nie wycofywal transakcji przez dodanie noRollbackFor

//    @Transactional(noRollbackFor = IllegalStateException.class)
//    public void playWithTransactions() {
//        log.info("-----------------------");
//        Author a1 = authorRepository.findById(1L).get();
//        a1.setFirstName("zmienione");
//        authorRepository.saveAndFlush(a1);
//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        log.info("-----------------------");
//        throw new IllegalStateException("Wycofanie transakcji");
//    }

//      Ustawienie checked exceptions zeby  wycofywal transakcji przez dodanie rollbackFor
//    @Transactional(rollbackFor = IOException.class)
//    public void playWithTransactions() throws IOException {
//        log.info("-----------------------");
//        Author a1 = authorRepository.findById(1L).get();
//        a1.setFirstName("zmienione1");
//        authorRepository.saveAndFlush(a1);
//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        log.info("-----------------------");
//        throw new IOException("Wycofanie transakcji");
//    }


    // SNEAKY THROWS
    // @SneakyThrows sprawia, że wyjątek checked (np. Exception) zachowuje się w locie bajtowym jak wyjątek niesprawdzany,
    // Spring potraktuje go tak samo jak RuntimeException i wykona wycofanie transakcji (rollback).

    @Transactional
    @SneakyThrows
    public void playWithTransactions() {
        log.info("-----------------------");
        Author a1 = authorRepository.findById(1L).get();
        a1.setFirstName("zmienione1");
        authorRepository.saveAndFlush(a1);
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("-----------------------");
        throw new IOException("Wycofanie transakcji");
    }
}
