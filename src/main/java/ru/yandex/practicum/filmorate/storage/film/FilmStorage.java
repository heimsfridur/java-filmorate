package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    public Collection<Film> getAllFilms();

    public Film addFilm(Film film);

    public Film updateFilm(Film newFilm);

    public Film getFilmById(int id);

    public void addLikeToFilm(int filmId, int userId);

    public boolean isFilmLikedByUser(int filmId, int userId);

    public void deleteLikeFromFilm(int filmId, int userId);

    public List<Film> getTopFilms(int count);

    public boolean isFilmExists(int filmId);
}
