package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

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
    public Film getById(@PathVariable int id) {
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
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Received a request to like a film");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Received a request to delete film");
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10")
                                       @Positive(message = "Amount of films must be positive") Integer count,
                                       @RequestParam(required = false) Integer genreId,
                                       @RequestParam(required = false) Integer year) {
        log.info(String.format("Received a request to get top %d films by %d genre and %d year", count, genreId, year));
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        log.info("Received a request to get list of common films by users {} and {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable(required = false) int directorId, @RequestParam(required = false, name = "sortBy") String paramSort) {
        log.info(String.format("Received films of director with id %s by %s sort", directorId, paramSort));
        return filmService.getFilmsOfDirectorBySort(directorId, paramSort);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable int id) {
        log.info(String.format("Received a request to delete film with id = %d", id));
        filmService.deleteById(id);
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