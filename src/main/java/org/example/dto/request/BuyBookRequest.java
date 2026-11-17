package org.example.dto.request;

import jakarta.validation.constraints.NotNull;

public record BuyBookRequest (@NotNull Long bookId) {
}
