package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(int id) {
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
        if (filmStorage.isFilmLikedByUser(filmId, userId)) {
            throw new IllegalArgumentException(String.format("User with ID %d has already liked film with ID %d",
                    userId, filmId));
        }

        filmStorage.addLikeToFilm(filmId, userId);
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
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
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
}
