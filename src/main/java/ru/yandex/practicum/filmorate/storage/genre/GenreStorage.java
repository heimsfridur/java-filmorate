package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    List<Genre> getAll();

    Genre getById(int id);

    void setGenresForFilm(Film film, Set<Genre> genres);

    void loadGenresForFilms(List<Film> films);

    boolean isExist(int genreId);

    boolean allGenresExist(Set<Genre> genres);

    List<Genre> getGenresListForFilm(Integer filmId);
}
