package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateBookRequest(
        @NotBlank @Size(min = 2, max = 255) String title,
        @NotBlank @Size(max = 255) String category,
        @NotNull Boolean available
) {
}
