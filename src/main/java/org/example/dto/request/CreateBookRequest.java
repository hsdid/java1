package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(
        @NotBlank @Size(min = 2, max = 255) String title,
        @NotNull Long authorId,
        @NotBlank @Size(max = 255) String category,
        @NotNull Boolean available
) {
}
