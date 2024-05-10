package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info(String.format("Film with id %d was added.", filmId - 1));
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film newFilm) {
        int filmId = newFilm.getId();

        if (films.containsKey(filmId)) {
            films.put(filmId, newFilm);
            log.info(String.format("Film with id %d was updated", filmId));
            return newFilm;
        }

        log.warn(String.format("Can not update film. There is no film with id %d", filmId));
        throw new NotFoundException(String.format("Film with id %d is not found", newFilm.getId()));
    }
}
