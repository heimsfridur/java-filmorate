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

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public Film addFilm(Film film) {
        if (film.getMpa() != null && !mpaStorage.isMpaExist(film.getMpa().getId())) {
            throw new ValidationException(String.format("Mpa with ID %d does not exist.", film.getMpa().getId()));
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (!genreStorage.isGenreExist(genre.getId())) {
                    throw new ValidationException(String.format("Genre with ID %d does not exist.", genre.getId()));
                }
            }
        }

        log.info(String.format("Film with name %s was added with ID %d.", film.getName(), film.getId()));
        return filmStorage.addFilm(film);
    }

    public void addLike(int filmId, int userId) {
        if (!filmStorage.isFilmExists(filmId)) {
            throw new NotFoundException(String.format("Film with ID %d does not exist.", filmId));
        }
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        if (filmStorage.isFilmLikedByUser(filmId, userId)) {
            throw new IllegalArgumentException(String.format("User with ID %d has already liked film with ID %d",
                    userId, filmId));
        }

        filmStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        if (!filmStorage.isFilmExists(filmId)) {
            throw new NotFoundException(String.format("Film with ID %d does not exist.", filmId));
        }
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        if (!filmStorage.isFilmLikedByUser(filmId, userId)) {
            throw new IllegalArgumentException(String.format("User with ID %d has not liked film with ID %d",
                    userId, filmId));
        }

        filmStorage.deleteLikeFromFilm(filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    public Film updateFilm(Film newFilm) {
        int filmId = newFilm.getId();
        if (!filmStorage.isFilmExists(filmId)) {
            log.warn(String.format("Can not update film. There is no film with id %d", filmId));
            throw new NotFoundException(String.format("Film with ID %d does not exist.", filmId));
        }
        return filmStorage.updateFilm(newFilm);
    }
}
