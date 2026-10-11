package org.example.exception;

public class BookTitleAlreadyExistsException extends RuntimeException {
    public BookTitleAlreadyExistsException(String title) {
        super("Book with title " + title + "already existr");
    }
}
