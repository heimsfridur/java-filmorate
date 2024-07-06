package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    Film add(Film film);

    Film update(Film newFilm);

    Film getById(int id);

    void addLikeToFilm(int filmId, int userId);

    boolean isFilmLikedByUser(int filmId, int userId);

    void deleteLikeFromFilm(int filmId, int userId);

    boolean isExists(int filmId);

    List<Film> getPopular(int count, Integer genre, Integer year);

    int getAmountOfLikes(Film film);

    List<Film> getCommonFilms(int userId, int friendId);

    void deleteById(int filmId);

    List<Film> getFilmsOfDirectorByLikesSorting(int directorId);

    List<Film> getFilmsOfDirectorByYearSorting(int directorId);

    List<Film> getRecommendations(int userId);

    List<Film> searchFilms(String query, String by);
}