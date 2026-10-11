package org.example.exception;

public class AuthorNotFoundException extends NotFoundException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}
