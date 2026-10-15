package org.example.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.CreateBookRequest;
import org.example.dto.request.PatchBookRequest;
import org.example.dto.request.UpdateBookRequest;
import org.example.dto.response.BookResponse;
import org.example.exception.AuthorNotFoundException;
import org.example.exception.BookNotFoundException;
import org.example.exception.BookTitleAlreadyExistsException;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.example.support.BookPageableSortTranslator;
import org.example.support.BookSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookPageableSortTranslator sortTranslator;

    @PostConstruct
    public void init() {
        Author a1 = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));
        Author a2 = authorRepository.saveAndFlush(new Author("Ewelina", "Damianska", 1900, 2000));
        bookRepository.saveAndFlush(new Book("Title1", "LEKTURA", true, a1));
        bookRepository.saveAndFlush(new Book("Title2", "LEKTURA", true, a2));
        bookRepository.saveAndFlush(new Book("Title3", "LEKTURA", true, a2));
    }


    public BookResponse createBook(CreateBookRequest request) {
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new AuthorNotFoundException(
                        "Author with id " + request.authorId() + " not found"
                ));

        Book savedBook = bookRepository.save(
                new Book(
                        request.title(),
                        request.category(),
                        true,
                        author
                )
        );

        return BookResponse.from(savedBook);
    }

    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(
                "Book with id " + id + " not found"
        ));

        bookRepository.delete(book);
    }

    @Transactional
    public void update(UpdateBookRequest request, Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));

        if (!book.getTitle().equals(request.title()) && bookRepository.existsByTitle(request.title())) {
            throw new BookTitleAlreadyExistsException(request.title());
        }

        book.setTitle(request.title());
        book.setAvailable(request.available());
        book.setCategory(request.category());
    }

    @Transactional
    public void partialUpdate(PatchBookRequest request, Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(
                "Book with id " + id + " not found"
        ));

        if (request.title() != null && !request.title().equals(book.getTitle())) {
            if (bookRepository.existsByTitle(request.title())) {
                throw new BookTitleAlreadyExistsException(request.title());
            }
            book.setTitle(request.title());
        }

        if (request.available() != null && request.available() != book.isAvailable()) {
            book.setAvailable(request.available());
        }

        if (request.category() != null && !request.category().equals(book.getCategory())) {
            book.setCategory(request.category());
        }
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getAll(
            Pageable pageable,
            String title,
            String category,
            Long authorId,
            Boolean available
    ) {
        Pageable sort = sortTranslator.translate(pageable);

        Specification<Book> spec = Specification.where(BookSpecifications.hasTitle(title))
                .and(BookSpecifications.hasCategory(category))
                .and(BookSpecifications.hasAuthorId(authorId))
                .and(BookSpecifications.isAvailable(available));

        return bookRepository.findAll(spec, sort).map(BookResponse::from);
    }

    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(
                        "Book with id " + id + " not found"
                ));

        return BookResponse.from(book);
    }

    @Transactional
    public void importBooks(byte[] bytes) {
        String content = new String(bytes);

        AtomicInteger counter = new AtomicInteger(0);
        AtomicLong start = new AtomicLong(System.currentTimeMillis());

        Arrays.stream(content.split("\r\n"))
                .map(line -> line.split(","))
                .map(args -> new CreateBookRequest(args[0], Long.parseLong(args[2]), args[1]))
                .peek(command -> countTime(counter, start))
                .forEach(this::createBook);
    }

    private void countTime(AtomicInteger counter, AtomicLong start) {
        if(counter.incrementAndGet() % 10000 == 0) {
            log.info("imported: {} in {} ms", counter, (System.currentTimeMillis() - start.get()));
            start.set(System.currentTimeMillis());
        }
    }
}
