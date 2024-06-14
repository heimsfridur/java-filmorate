package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(filmId++);
        film.setLikesFromUsers(new HashSet<>());
        films.put(film.getId(), film);
        log.info(String.format("Film with id %d was added.", filmId - 1));
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        int filmId = newFilm.getId();
        Film oldFilm = getFilmById(filmId);

        if (!films.containsKey(filmId)) {
            log.warn(String.format("Can not update film. There is no film with id %d", filmId));
            throw new NotFoundException(String.format("Film with id %d is not found", newFilm.getId()));

        }
        newFilm.setLikesFromUsers(oldFilm.getLikesFromUsers());

        films.put(filmId, newFilm);
        log.info(String.format("Film with id %d was updated", filmId));
        return newFilm;
      }

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            log.warn(String.format("Can't find film with id %d", id));
            throw new NotFoundException(String.format("Can't find film with id %d", id));
        }
        return films.get(id);
    }
}

