package org.example.dto.response;

import org.example.model.Author;

public record AuthorResponse(Long id, String firstName, String lastName) {

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName()
        );
    }
}
