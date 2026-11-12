package org.example.dto.response;

import lombok.Getter;
import org.example.model.Book;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class BookResponse extends RepresentationModel<BookResponse> {

    private Long id;
    private String title;
    private Long authorId;
    private String category;
    private boolean available;

    public BookResponse(Long id, String title, Long authorId, String category, boolean available) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.category = category;
        this.available = available;
    }

    public static BookResponse from(Book book) {


        return new BookResponse(book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getCategory(),
                book.isAvailable());
    }
}
