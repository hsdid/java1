package org.example.assembler;

import org.example.controller.AuthorController;
import org.example.controller.BookController;
import org.example.dto.response.BookResponse;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<BookResponse, BookResponse> {

    @Override
    public BookResponse toModel(BookResponse book) {

        book.add(
                linkTo(methodOn(BookController.class)
                        .getBookByID(book.getId()))
                        .withSelfRel()
        );

        book.add(
                linkTo(methodOn(AuthorController.class)
                        .getAuthor(book.getAuthorId()))
                        .withRel("author")
        );

        return book;
    }
}
