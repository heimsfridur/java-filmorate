package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.AddException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.sql.Date;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Slf4j
@Component
@Qualifier
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final GenreDbStorage genreDbStorage;

    @Override
    public List<Film> getAll() {
        String sql = "SELECT films.*, mpa.mpa_id, mpa.mpa_name FROM films LEFT JOIN mpa ON films.film_mpa = mpa.mpa_id;";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        genreDbStorage.loadGenresForFilms(films);

        log.info("Got all films.");
        return films;
    }

    @Override
    public Film add(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO films (film_name, film_description, " +
                "film_releaseDate, film_duration, film_mpa) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if (key != null) {
            film.setId(key.intValue());
        } else {
            throw new AddException("Failed to add film");
        }

        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            genreDbStorage.setGenresForFilm(film, genres);
        }

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        int filmId = newFilm.getId();

        String sql = "UPDATE films SET film_name = ?, film_description = ?, film_releaseDate = ?, " +
                "film_duration = ?, film_mpa = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sql, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(),
                newFilm.getDuration(), newFilm.getMpa().getId(), filmId);
        log.info(String.format("Film with id %d was updated", filmId));
        return newFilm;
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT films.*, MPA.* FROM films " +
                "LEFT JOIN MPA ON films.film_mpa = MPA.mpa_id WHERE film_id = ? LIMIT 1";

        Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);

        genreDbStorage.loadGenresForFilms(List.of(film));


        return film;
    }

    @Override
    public boolean isExists(int filmId) {
        String sql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count != null && count > 0;
    }

    @Override
    public void addLikeToFilm(int filmId, int userId) {
        String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public boolean isFilmLikedByUser(int filmId, int userId) {
        String sql = "SELECT COUNT(*) FROM films_likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        return count != null && count > 0;
    }

    @Override
    public void deleteLikeFromFilm(int filmId, int userId) {
        String sql = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = "SELECT films.* , MPA.* " +
                "FROM films LEFT JOIN films_likes ON films.film_id = films_likes.film_id " +
                "LEFT JOIN MPA ON films.film_mpa = MPA.mpa_id " +
                "GROUP BY films.film_id " +
                "ORDER BY COUNT(films_likes.user_id) DESC " +
                "LIMIT ?";
        List<Film> topFilms = jdbcTemplate.query(sql, filmRowMapper, count);

        genreDbStorage.loadGenresForFilms(topFilms);

        return topFilms;
    }

    @Override
    public int getAmountOfLikes(Film film) {
        String sql = "SELECT COUNT(*) " +
                "FROM films_likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, film.getId());
    }

    public List<Film> searchFilms(String query, String by) {
        StringBuilder sql = new StringBuilder("SELECT films.* "
                + "FROM films "
                + "LEFT JOIN films_likes ON films.film_id = films_likes.film_id "
                + "LEFT JOIN mpa ON mpa.mpa_id = films.mpa_id "
                + "LEFT JOIN films_directors ON films.film_id = films_directors.film_id "
                + "LEFT JOIN directors ON films_directors.director_id = directors.director_id ");
        switch (by) {
            case ("title"):
                sql.append("WHERE LOWER(films.film_name) LIKE LOWER('%").append(query).append("%') ");
                break;
            case ("director"):
                sql.append("WHERE LOWER(directors.director_name) LIKE LOWER('%").append(query).append("%') ");
                break;
            case ("title,director"), ("director,title"):
                sql.append("WHERE LOWER(films.film_name) LIKE LOWER('%").append(query).append("%') ");
                sql.append("OR LOWER(directors.director_name) LIKE LOWER('%").append(query).append("%') ");
                break;
        }
        sql.append("GROUP BY films.film_id, films_likes.film_id " + "ORDER BY COUNT(films_likes.film_id) DESC");
        return jdbcTemplate.query(sql.toString(), filmRowMapper);
    }
}
