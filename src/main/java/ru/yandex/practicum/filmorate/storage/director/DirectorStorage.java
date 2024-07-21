package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {

    Director addDirector(Director director);

    Director updateDirector(Director director);

    Director getDirectorById(Integer id);

    List<Director> getDirectors();

    void checkDirector(Integer directorId);

    boolean deleteDirector(Integer directorId);

    void setDirectorsForFilm(List<Director> directors, int filmId);

    void loadDirectorsForFilms(List<Film> films);

    List<Director> getDirectorListFromFilm(Integer filmId);
}
