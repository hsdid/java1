package org.example.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Main;
import org.example.dto.request.CreateBookRequest;
import org.example.dto.request.PatchBookRequest;
import org.example.dto.request.UpdateBookRequest;
import org.example.dto.response.BookResponse;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerTest {
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc postman;

    @Test
    void shouldReturnSingleBook() throws Exception {
        Author author = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));
        Book book = bookRepository.saveAndFlush(new Book("Title1", "LEKTURA", true, author));
        Long id = book.getId();

        postman.perform(get("/api/v1/books/" + +id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.category").value(book.getCategory()))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldReturnBookList() throws Exception {
        Author author = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));
        bookRepository.saveAndFlush(new Book("Title1", "LEKTURA", true, author));
        bookRepository.saveAndFlush(new Book("Title2", "LEKTURA2", true, author));
        bookRepository.saveAndFlush(new Book("Title3", "LEKTURA3", true, author));

        String responseJson = postman.perform(get("/api/v1/books")
                        .param("size", "3")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode content = objectMapper.readTree(responseJson).get("content");
        List<BookResponse> books = objectMapper.convertValue(content, new TypeReference<>() {
        });

        assertThat(books).hasSize(3)
                .extracting(BookResponse::title)
                .containsExactlyInAnyOrder("Title1", "Title2", "Title3");

        assertThat(books).allSatisfy(bookResponse -> {
            assertNotNull(bookResponse.id());
            assertNotNull(bookResponse.title());
            assertNotNull(bookResponse.authorId());
            assertNotNull(bookResponse.category());
            assertEquals(author.getId(), bookResponse.authorId());
        });
    }


    @Test
    void shouldSaveAndReturnSingleBook() throws Exception {
        Author author = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));

        CreateBookRequest dto = new CreateBookRequest(
                "Pan Tadeusz",
                author.getId(),
                "Lektura",
                true
        );

        String requestBody = objectMapper.writeValueAsString(dto);

        String responseJson = postman.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookResponse responseDto = objectMapper.readValue(responseJson, BookResponse.class);

        Book savedBook = bookRepository.findById(responseDto.id()).get();

        Assertions.assertEquals(dto.title(), savedBook.getTitle());


        postman.perform(get("/api/v1/books/" + +responseDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.id()))
                .andExpect(jsonPath("$.title").value(responseDto.title()))
                .andExpect(jsonPath("$.category").value(responseDto.category()))
                .andExpect(jsonPath("$.authorId").value(responseDto.authorId()))
                .andExpect(jsonPath("$.available").value(responseDto.available()));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        Author author = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));
        Book book = bookRepository.saveAndFlush(new Book("Title1", "LEKTURA", true, author));
        Long id = book.getId();

        UpdateBookRequest dto = new UpdateBookRequest("Zmieniony Tytuł", "POEZJA", false);
        String requestBody = objectMapper.writeValueAsString(dto);

        postman.perform(put("/api/v1/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        postman.perform(get("/api/v1/books/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Zmieniony Tytuł"))
                .andExpect(jsonPath("$.category").value("POEZJA"))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.authorId").value(author.getId()));
    }

    @Test
    void shouldPartialUpdateBook() throws Exception {
        Author author = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));
        Book book = bookRepository.saveAndFlush(new Book("Title1", "LEKTURA", true, author));
        Long id = book.getId();

        PatchBookRequest dto = new PatchBookRequest(null, "NOWA_KATEGORIA", null);
        String requestBody = objectMapper.writeValueAsString(dto);

        postman.perform(patch("/api/v1/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        postman.perform(get("/api/v1/books/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title1"))
                .andExpect(jsonPath("$.category").value("NOWA_KATEGORIA"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        Author author = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));
        Book book = bookRepository.saveAndFlush(new Book("Title1", "LEKTURA", true, author));
        Long id = book.getId();

        postman.perform(delete("/api/v1/books/" + +id))
                .andExpect(status().isNoContent());


        postman.perform(get("/api/v1/books/" + +id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenBookDoesNotExist() throws Exception {
        postman.perform(get("/api/v1/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Book with id 999 not found"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void shouldReturn404WhenUpdatingNotExistingBook() throws Exception {
        UpdateBookRequest dto = new UpdateBookRequest("Nowy Tytuł", "LEKTURA", true);
        String requestBody = objectMapper.writeValueAsString(dto);

        postman.perform(put("/api/v1/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Book with id 999 not found"));
    }

    @Test
    void shouldReturn404WhenPartiallyUpdatingNotExistingBook() throws Exception {
        PatchBookRequest dto = new PatchBookRequest(null, "LEKTURA", null);
        String requestBody = objectMapper.writeValueAsString(dto);

        postman.perform(patch("/api/v1/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturn404WhenDeletingNotExistingBook() throws Exception {
        postman.perform(delete("/api/v1/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Book with id 999 not found"));
    }

    @Test
    void shouldReturn400WhenTitleAlreadyExists() throws Exception {
        Author author = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));
        bookRepository.saveAndFlush(new Book("Zajety Tytul", "LEKTURA", true, author));

        CreateBookRequest dto = new CreateBookRequest("Zajety Tytul", author.getId(), "LEKTURA", true);
        String requestBody = objectMapper.writeValueAsString(dto);

        postman.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Zajety Tytul")))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void shouldReturn400WhenCreateBookRequestIsInvalid() throws Exception {
        CreateBookRequest dto = new CreateBookRequest("", null, "", null);
        String requestBody = objectMapper.writeValueAsString(dto);

        postman.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[?(@.field == 'title')]").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.field == 'authorId')]").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.field == 'category')]").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.field == 'available')]").isNotEmpty());
    }

    @Test
    void shouldSortByAuthorIdFieldThatIsNotInEntity() throws Exception {
        Author first = authorRepository.saveAndFlush(new Author("First", "Sorter", 1900, 2000));
        Author second = authorRepository.saveAndFlush(new Author("Second", "Sorter", 1901, 2001));
        bookRepository.saveAndFlush(new Book("SortBookFirst", "LEKTURA", true, first));
        bookRepository.saveAndFlush(new Book("SortBookSecond", "LEKTURA", true, second));

        List<BookResponse> desc = getBooksSortedBy("authorId,desc");
        List<Long> descAuthorIds = desc.stream().map(BookResponse::authorId).toList();
        assertThat(descAuthorIds).isSortedAccordingTo(Comparator.reverseOrder());
        assertThat(indexOfAuthorId(desc, second.getId()))
                .isLessThan(indexOfAuthorId(desc, first.getId()));

        List<BookResponse> asc = getBooksSortedBy("authorId,asc");
        List<Long> ascAuthorIds = asc.stream().map(BookResponse::authorId).toList();
        assertThat(ascAuthorIds).isSorted();
        assertThat(indexOfAuthorId(asc, first.getId()))
                .isLessThan(indexOfAuthorId(asc, second.getId()));
    }

    private List<BookResponse> getBooksSortedBy(String sort) throws Exception {
        String body = postman.perform(get("/api/v1/books")
                        .param("size", "1000")
                        .param("sort", sort))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode content = objectMapper.readTree(body).get("content");
        return objectMapper.convertValue(content, new TypeReference<>() {
        });
    }

    private int indexOfAuthorId(List<BookResponse> books, Long authorId) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).authorId().equals(authorId)) {
                return i;
            }
        }
        throw new AssertionError("Book with authorId " + authorId + " not found in response");
    }

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {
        Author author = authorRepository.saveAndFlush(new Author("Adam", "Adamski", 1900, 2000));
        Book book = bookRepository.saveAndFlush(new Book("Title1", "LEKTURA", true, author));

        UpdateBookRequest dto = new UpdateBookRequest("X", "", null);
        String requestBody = objectMapper.writeValueAsString(dto);

        postman.perform(put("/api/v1/books/" + book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[?(@.field == 'title')]").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.field == 'category')]").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.field == 'available')]").isNotEmpty());
    }

}