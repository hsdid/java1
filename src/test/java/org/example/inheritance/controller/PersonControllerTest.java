package org.example.inheritance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Main;
import org.example.inheritance.dto.command.CreatePersonCommand;
import org.example.inheritance.model.PersonParameter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnBadRequestWhenPersonTypeIsInvalid() throws Exception {
        CreatePersonCommand command = new CreatePersonCommand("invalid_type", List.of(
                new PersonParameter("name", "John"),
                new PersonParameter("age", "20")
        ));

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEmployeeMissingParameters() throws Exception {
        CreatePersonCommand command = new CreatePersonCommand("employee", List.of(
                new PersonParameter("name", "John")
                // missing salary, position, age
        ));

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenSalaryIsNotANumber() throws Exception {
        CreatePersonCommand command = new CreatePersonCommand("employee", List.of(
                new PersonParameter("name", "John"),
                new PersonParameter("age", "30"),
                new PersonParameter("position", "Manager"),
                new PersonParameter("salary", "not-a-number")
        ));

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }
}
