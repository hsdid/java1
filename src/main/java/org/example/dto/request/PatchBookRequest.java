package org.example.dto.request;

import jakarta.validation.constraints.Size;

public record PatchBookRequest(
        @Size(min = 2, max = 255) String title,
        @Size(max = 255) String category,
        Boolean available
) {
}
