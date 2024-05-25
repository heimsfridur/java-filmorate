package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.awt.*;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(int filmId, int userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (!user.getLikedFilms().contains(filmId)) {
            user.getLikedFilms().add(filmId);
            film.getLikesFromUsers().add(userId);
            log.info(String.format("Add like from user with id %d to film with id %d", userId, filmId));
        } else {
            log.info(String.format("User %d has already liked film %d", userId, filmId));
        }
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (film.getLikesFromUsers().contains(userId)) {
            user.getLikedFilms().remove(filmId);
            film.getLikesFromUsers().remove(userId);
        }
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        int filmsCount = filmStorage.getAllFilms().size();
        if (count > filmsCount) {
            count = filmsCount;
        }

        List<Film> list =  filmStorage.getAllFilms().stream()
                .sorted((film1, film2) ->
                        film2.getLikesFromUsers().size() - film1.getLikesFromUsers().size())
                .limit(count)
                .toList();
        return list;
    }
}
