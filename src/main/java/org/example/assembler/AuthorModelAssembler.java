package org.example.assembler;

import org.example.controller.AuthorController;
import org.example.dto.response.AuthorResponse;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthorModelAssembler implements RepresentationModelAssembler<AuthorResponse, AuthorResponse> {

    @Override
    public AuthorResponse toModel(AuthorResponse author) {

        author.add(
                linkTo(methodOn(AuthorController.class)
                        .getAuthor(author.getId()))
                        .withSelfRel()
        );

        if (author.getNumberOfBooks() > 0) {
            author.add(
                    linkTo(methodOn(AuthorController.class)
                            .getAuthorBooks(author.getId()))
                            .withRel("author_books")
            );
        }

        author.add(
                linkTo(methodOn(AuthorController.class)
                        .getAuthors())
                        .withRel("authors")
        );

        return author;
    }
}