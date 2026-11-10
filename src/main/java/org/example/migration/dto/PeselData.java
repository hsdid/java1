package org.example.migration.dto;

import java.time.LocalDate;

public record PeselData(LocalDate birthDate, String gender) {
}
