package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmStorage filmStorage;
    @MockBean
    private FilmService filmService;

    @Test
    void shouldAddFilm() throws Exception {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Film film = Film.builder()
                .name("film1")
                .description("descr1")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .mpa(mpa)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String requestBody = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateFilm() throws Exception {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Film film = Film.builder()
                .name("film1")
                .description("descr1")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .mpa(mpa)
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
                .mpa(mpa)
                .build();
        String requestBody2 = objectMapper.writeValueAsString(updatedFilm);

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(requestBody2))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCommonFilms() throws Exception {
        int userId = 1;
        int friendId = 2;

        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Film film = Film.builder()
                .name("film1")
                .description("descr1")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .mpa(mpa)
                .build();

        List<Film> expectedFilms = Collections.singletonList(film);
        when(filmService.getCommonFilms(userId, friendId)).thenReturn(expectedFilms);

        mockMvc.perform(get("/films/common")
                        .param("userId", String.valueOf(userId))
                        .param("friendId", String.valueOf(friendId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedFilms)));
    }
}