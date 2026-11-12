package org.example.dto.response;

import lombok.Getter;
import org.example.model.Author;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class AuthorResponse extends RepresentationModel<AuthorResponse> {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer numberOfBooks;

    public AuthorResponse(Long id, String firstName, String lastName, Integer numberOfBooks) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.numberOfBooks = numberOfBooks;
    }

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getBooks().size()
        );
//        ).addLinks();
    }

    // addLinks()
}
