package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Received a request to get all films");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Integer id) {
        log.info(String.format("Received a request to get film with id %d", id));
        return filmService.getById(id);
    }

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        log.info("Received a request to add a film");
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Received a request to update a film");
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Received a request to like a film");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Received a request to delete film");
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10")
                                           @Positive(message = "Amount of films must be positive") Integer count,
                                       @RequestParam(required = false) Integer genre,
                                       @RequestParam(required = false) Integer year) {
        log.info(String.format("Received a request to get top %d films", count));
        return filmService.getPopular(count, genre, year);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(@RequestParam
                                       @NotBlank(message = "Param <<query>> must be not empty") String query,
                                       @RequestParam
                                       @NotBlank(message = "Param <<by>> must be not empty") String by) {
        log.info(String.format("Received a request to GET /search %s by %s", query, by));
        return filmService.searchFilms(query, by);
    }

}
