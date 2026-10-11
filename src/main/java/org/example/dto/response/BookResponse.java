package org.example.dto.response;

import org.example.model.Book;


public record BookResponse(Long id, String title, Long authorId, String category, boolean available) {

    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getCategory(),
                book.isAvailable());
    }
}
