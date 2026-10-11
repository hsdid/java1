package org.example.dto.response;

import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<ErrorDetail> errors
) {
}
