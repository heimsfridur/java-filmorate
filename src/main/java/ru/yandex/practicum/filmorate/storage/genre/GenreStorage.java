package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    public List<Genre> getAll();

    public Genre getById(int id);

    public void setGenresForFilm(Film film, Set<Genre> genres);

    public void loadGenresForFilms(List<Film> films);

    public boolean isExist(int genreId);

    public boolean allGenresExist(Set<Genre> genres);

    List<Genre> getGenresListForFilm(Integer filmId);
}
