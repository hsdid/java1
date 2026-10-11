package org.example.dto.response;

public record ErrorDetail(
        String field,
        String message
) {
}
