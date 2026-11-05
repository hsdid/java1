package org.example.inheritance.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.example.inheritance.model.PersonParameter;
import org.example.inheritance.validation.PersonType;
import org.example.inheritance.validation.ValidPersonParameters;

import java.util.List;

// todo nastepnie dodaj walidacje typu osoby
@ValidPersonParameters
public record CreatePersonCommand(
        @PersonType @NotBlank String personType,
        @NotNull List<PersonParameter> parameters) {
}
