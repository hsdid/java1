package org.example.exception;

public class BookNotAvailableOrNotFoundException extends RuntimeException {
    public BookNotAvailableOrNotFoundException() {
    }

    public BookNotAvailableOrNotFoundException(String message) {
        super(message);
    }
}
