package org.example.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Main;
import org.example.dto.response.AuthorResponse;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorControllerTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc postman;

    @Test
    void shouldReturnPagedAuthors() throws Exception {
        Author a0 = authorRepository.saveAndFlush(new Author("Paged", "Author0", 1900, 2000));
        Author a1 = authorRepository.saveAndFlush(new Author("Paged", "Author1", 1901, 2001));
        Author a2 = authorRepository.saveAndFlush(new Author("Paged", "Author2", 1902, 2002));
        Author a3 = authorRepository.saveAndFlush(new Author("Paged", "Author3", 1903, 2003));
        Author a4 = authorRepository.saveAndFlush(new Author("Paged", "Author4", 1904, 2004));

        String firstPageBody = postman.perform(get("/api/v1/authors")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.totalElements", org.hamcrest.Matchers.greaterThanOrEqualTo(5)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<AuthorResponse> firstPage = readContent(firstPageBody);
        assertThat(firstPage).extracting(AuthorResponse::id)
                .containsExactly(a4.getId(), a3.getId());

        String secondPageBody = postman.perform(get("/api/v1/authors")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<AuthorResponse> secondPage = readContent(secondPageBody);
        assertThat(secondPage).extracting(AuthorResponse::id)
                .containsExactly(a2.getId(), a1.getId());

        String thirdPageBody = postman.perform(get("/api/v1/authors")
                        .param("page", "2")
                        .param("size", "2")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.numberOfElements", org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<AuthorResponse> thirdPage = readContent(thirdPageBody);
        assertThat(thirdPage).extracting(AuthorResponse::id).contains(a0.getId());
    }

    @Test
    void shouldReturnDefaultPageSizeWhenNoParamsProvided() throws Exception {
        postman.perform(get("/api/v1/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    void shouldReturnNumberOfBooksForEachAuthor() throws Exception {
        Author author = authorRepository.saveAndFlush(new Author("With", "Books", 1900, 2000));
        bookRepository.saveAndFlush(new Book("PagedBook1", "LEKTURA", true, author));
        bookRepository.saveAndFlush(new Book("PagedBook2", "LEKTURA", true, author));

        String body = postman.perform(get("/api/v1/authors")
                        .param("size", "1")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(author.getId()))
                .andExpect(jsonPath("$.content[0].firstName").value("With"))
                .andExpect(jsonPath("$.content[0].lastName").value("Books"))
                .andExpect(jsonPath("$.content[0].numberOfBooks").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<AuthorResponse> authors = readContent(body);
        assertThat(authors).hasSize(1);
    }

    @Test
    void shouldSortByNumberOfBooksFieldThatIsNotInEntity() throws Exception {
        Author fewBooks = authorRepository.saveAndFlush(new Author("Ala", "FewBooks", 1900, 2000));
        bookRepository.saveAndFlush(new Book("FewBook1", "LEKTURA", true, fewBooks));

        Author manyBooks = authorRepository.saveAndFlush(new Author("Bea", "ManyBooks", 1901, 2001));
        bookRepository.saveAndFlush(new Book("ManyBook1", "LEKTURA", true, manyBooks));
        bookRepository.saveAndFlush(new Book("ManyBook2", "LEKTURA", true, manyBooks));
        bookRepository.saveAndFlush(new Book("ManyBook3", "LEKTURA", true, manyBooks));

        List<AuthorResponse> desc = getAuthorsSortedBy("numberOfBooks,desc");
        List<Integer> descCounts = desc.stream().map(AuthorResponse::numberOfBooks).toList();
        assertThat(descCounts).isSortedAccordingTo(Comparator.reverseOrder());
        assertThat(indexOfAuthor(desc, manyBooks.getId()))
                .isLessThan(indexOfAuthor(desc, fewBooks.getId()));

        List<AuthorResponse> asc = getAuthorsSortedBy("numberOfBooks,asc");
        List<Integer> ascCounts = asc.stream().map(AuthorResponse::numberOfBooks).toList();
        assertThat(ascCounts).isSorted();
        assertThat(indexOfAuthor(asc, fewBooks.getId()))
                .isLessThan(indexOfAuthor(asc, manyBooks.getId()));
    }

    private List<AuthorResponse> getAuthorsSortedBy(String sort) throws Exception {
        String body = postman.perform(get("/api/v1/authors")
                        .param("size", "1000")
                        .param("sort", sort))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return readContent(body);
    }

    private int indexOfAuthor(List<AuthorResponse> authors, Long id) {
        for (int i = 0; i < authors.size(); i++) {
            if (authors.get(i).id().equals(id)) {
                return i;
            }
        }
        throw new AssertionError("Author with id " + id + " not found in response");
    }

    private List<AuthorResponse> readContent(String body) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        return objectMapper.convertValue(root.get("content"), new TypeReference<>() {
        });
    }
}
