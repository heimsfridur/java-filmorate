package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public List<Film> getAll();

    public Film add(Film film);

    public Film update(Film newFilm);

    public Film getById(int id);

    public void addLikeToFilm(int filmId, int userId);

    public boolean isFilmLikedByUser(int filmId, int userId);

    public void deleteLikeFromFilm(int filmId, int userId);

    public List<Film> getPopular(int count);

    public boolean isExists(int filmId);

    public int getAmountOfLikes(Film film);

    public List<Film> searchFilms(String query, String by);
}
