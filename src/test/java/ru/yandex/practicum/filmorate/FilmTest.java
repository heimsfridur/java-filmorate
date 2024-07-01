package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.model.Film;

import jakarta.validation.Validator;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FilmTest {
    @Autowired
    private Validator validator;

    @Test
    public void shouldBeValidFilm() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

         Film film = Film.builder()
                .name("film1")
                .description("descr1")
                .releaseDate(LocalDate.of(2014, 10, 11))
                .duration(150)
                .mpa(mpa)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size());
    }

    @Test
    public void shouldBeNotValidWhenNameIsEmpty() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Film film = Film.builder()
                .description("descr1")
                .releaseDate(LocalDate.of(2014, 10, 11))
                .duration(150)
                .mpa(mpa)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldBeNotValidWhenDesccriptionIsLong() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        String longString = "x".repeat(201);
        Film film = Film.builder()
                .name("name")
                .description(longString)
                .releaseDate(LocalDate.of(2014, 10, 11))
                .duration(150)
                .mpa(mpa)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldBeNotValidWhenEarlyReleaseDate() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Film film = Film.builder()
                .name("name")
                .description("descr")
                .releaseDate(LocalDate.of(1790, 10, 11))
                .duration(150)
                .mpa(mpa)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldBeNotValidWhenNegativeDuration() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Film film = Film.builder()
                .name("name")
                .description("descr")
                .releaseDate(LocalDate.of(2014, 10, 11))
                .duration(-150)
                .mpa(mpa)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }
}
