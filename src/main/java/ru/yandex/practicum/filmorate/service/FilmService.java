package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final EventService eventService;
    private final DirectorStorage directorStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(int id) {
        if (!filmStorage.isExists(id)) {
            throw new NotFoundException(String.format("Film with ID %d does not exist.", id));
        }
        return filmStorage.getById(id);
    }


    public Film add(Film film) {
        if (film.getMpa() != null && !mpaStorage.isExist(film.getMpa().getId())) {
            throw new ValidationException(String.format("Mpa with ID %d does not exist.", film.getMpa().getId()));
        }

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            if (!genreStorage.allGenresExist(genres)) {
                throw new ValidationException("Not all genres exist.");
            }
        }

        log.info(String.format("Film with name %s was added with ID %d.", film.getName(), film.getId()));
        return filmStorage.add(film);
    }

    public void addLike(int filmId, int userId) {
        if (!filmStorage.isExists(filmId)) {
            throw new NotFoundException(String.format("Film with ID %d does not exist.", filmId));
        }
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }

        filmStorage.addLikeToFilm(filmId, userId);
        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        if (!filmStorage.isExists(filmId)) {
            throw new NotFoundException(String.format("Film with ID %d does not exist.", filmId));
        }
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        if (!filmStorage.isFilmLikedByUser(filmId, userId)) {
            throw new IllegalArgumentException(String.format("User with ID %d has not liked film with ID %d",
                    userId, filmId));
        }

        filmStorage.deleteLikeFromFilm(filmId, userId);
        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
    }

    public Collection<Film> getPopular(int count, Integer genre, Integer year) {
        return filmStorage.getPopular(count, genre, year);
    }

    public Film update(Film newFilm) {
        int filmId = newFilm.getId();
        if (!filmStorage.isExists(filmId)) {
            log.warn(String.format("Can not update film. There is no film with id %d", filmId));
            throw new NotFoundException(String.format("Film with ID %d does not exist.", filmId));
        }
        return filmStorage.update(newFilm);
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        checkUserId(userId);
        checkUserId(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    private void checkUserId(int userId) {
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
    }

    public List<Film> getFilmsOfDirectorBySort(Integer directorId, String paramSort) {
        if (directorStorage.getDirectorById(directorId) == null) {
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Director not found!");
        }
        if (paramSort.equalsIgnoreCase("year")) {
            return filmStorage.getFilmsOfDirectorByYearSorting(directorId);
        }
        if (paramSort.equalsIgnoreCase("likes")) {
            return filmStorage.getFilmsOfDirectorByLikesSorting(directorId);
        }
        throw new InvalidParameterException("Unknown sorting parameter passed: " + paramSort);
    }

    public void deleteById(int filmId) {
        if (!filmStorage.isExists(filmId)) {
            log.warn(String.format("There is no film with id %d", filmId));
            throw new NotFoundException(String.format("Film with ID %d does not exist.", filmId));
        }
        filmStorage.deleteById(filmId);
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }
}