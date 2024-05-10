package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldAddFilm() throws Exception {
        Film film = Film.builder()
                .name("film1")
                .description("descr1")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String requestBody = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldUpdateFilm() throws Exception {
        Film film = Film.builder()
                .name("film1")
                .description("descr1")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String requestBody = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(requestBody));

        Film updatedFilm = Film.builder()
                .id(1)
                .name("NEW FILM")
                .description("!!!!!!!")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .build();
        String requestBody2 = objectMapper.writeValueAsString(updatedFilm);

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(requestBody2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NEW FILM"));
    }


 }
