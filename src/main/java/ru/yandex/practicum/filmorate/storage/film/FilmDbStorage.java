package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.AddException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Component
@Qualifier
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorDbStorage;

    @Override
    public List<Film> getAll() {
        String sql = "SELECT films.*, mpa.mpa_id, mpa.mpa_name FROM films LEFT JOIN mpa ON films.film_mpa = mpa.mpa_id;";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
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

        List<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            directorDbStorage.setDirectorsForFilm(directors, film.getId());
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

        updateGenres(newFilm);

        Set<Genre> updatedGenres = new HashSet<>(genreDbStorage.getGenresListForFilm(filmId));
        newFilm.setGenres(updatedGenres);

        log.info(String.format("Film with id %d was updated", filmId));
        return newFilm;
    }

    public void updateGenres(Film film) {
        Set<Genre> genres = film.getGenres();
        String sqlDelQuery = "DELETE " +
                "FROM films_genres " +
                "WHERE film_id = ?";
        if (genres != null && !genres.isEmpty()) {
            jdbcTemplate.update(sqlDelQuery, film.getId());
            genreDbStorage.setGenresForFilm(film, genres);
        } else if (genres.isEmpty()) {
            jdbcTemplate.update(sqlDelQuery, film.getId());
        }
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT films.*, MPA.* FROM films " +
                "LEFT JOIN MPA ON films.film_mpa = MPA.mpa_id WHERE film_id = ? LIMIT 1";

        Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
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
    public List<Film> getPopular(int count, Integer genre, Integer year) {

        StringBuilder sqlQuery = new StringBuilder(
                "SELECT films.*, mpa.*, COUNT(films_likes.user_id) AS likes_count " +
                        "FROM films " +
                        "LEFT JOIN films_likes ON films.film_id = films_likes.film_id " +
                        "LEFT JOIN mpa ON films.film_mpa = mpa.mpa_id " +
                        "LEFT JOIN films_genres ON films.film_id = films_genres.film_id ");

        boolean hasCondition = false;
        if (genre != null) {
            sqlQuery.append("WHERE films_genres.genre_id = ").append(genre).append(" ");
            hasCondition = true;
        }
        if (year != null) {
            if (hasCondition) {
                sqlQuery.append("AND ");
            } else {
                sqlQuery.append("WHERE ");
            }
            sqlQuery.append("YEAR(films.film_releaseDate) = ").append(year).append(" ");
        }

        sqlQuery.append("GROUP BY films.film_id ")
                .append("ORDER BY likes_count DESC, films.film_id ")
                .append("LIMIT ").append(count);


        List<Film> films = jdbcTemplate.query(sqlQuery.toString(), filmRowMapper);
        return films;
    }

    @Override
    public int getAmountOfLikes(Film film) {
        String sql = "SELECT COUNT(*) " +
                "FROM films_likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, film.getId());
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.*, m.mpa_id, m.mpa_name, COUNT(fl.user_id) AS likes " +
                "FROM films f " +
                "JOIN films_likes fl ON f.film_id = fl.film_id " +
                "JOIN mpa m ON f.film_mpa = m.mpa_id " +
                "WHERE fl.user_id IN (?, ?) " +
                "GROUP BY f.film_id, m.mpa_id " +
                "HAVING COUNT(DISTINCT fl.user_id) = 2 " +
                "ORDER BY likes DESC";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, userId, friendId);
        return films;
    }

    @Override
    public void deleteById(int filmId) {
        String sql = "DELETE FROM films WHERE film_id = ? ";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Film> getFilmsOfDirectorByLikesSorting(int directorId) {
        String sql = "SELECT films.*, MPA.* " +
                "FROM films_directors " +
                "LEFT JOIN films ON films.film_id = films_directors.film_id " +
                "LEFT JOIN films_likes ON films_likes.film_id = films_directors.film_id " +
                "LEFT JOIN MPA ON films.film_mpa = MPA.mpa_id " +
                "WHERE director_id =? " +
                "GROUP BY films.film_id " +
                "ORDER BY COUNT(films_likes.user_id) DESC ";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, directorId);
        return films;
    }

    @Override
    public List<Film> getFilmsOfDirectorByYearSorting(int directorId) {
        String sql = "SELECT films.*, MPA.* " +
                "FROM films_directors " +
                "LEFT JOIN films ON films.film_id = films_directors.film_id " +
                "LEFT JOIN MPA ON films.film_mpa = MPA.mpa_id " +
                "WHERE director_id =? " +
                "ORDER BY films.film_releaseDate ";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, directorId);
        return films;
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        String sql = """
                       SELECT fl.film_id,
                       fl.film_name,
                       fl.film_description,
                       fl.film_releaseDate,
                       fl.film_duration,
                       m.mpa_id AS rating_id,
                       m.mpa_name AS rating_name,
                       g.genre_id AS genre_id,
                       g.genre_name AS genre_name,
                       d.director_id AS director_id,
                       d.director_name AS director_name
                FROM films_likes flk
                JOIN films fl ON flk.film_id = fl.film_id
                LEFT JOIN mpa m ON m.mpa_id = fl.film_mpa
                LEFT JOIN films_genres fg ON fl.film_id = fg.film_id
                LEFT JOIN genres g ON g.genre_id = fg.genre_id
                LEFT JOIN films_directors fd ON fl.film_id = fd.film_id
                LEFT JOIN directors d ON d.director_id = fd.director_id
                WHERE flk.user_id IN (
                    SELECT flk2.user_id
                    FROM films_likes flk2
                    WHERE flk2.film_id IN (
                        SELECT flk3.film_id
                        FROM films_likes flk3
                        WHERE flk3.user_id = ?)
                        AND flk2.user_id <> ?
                    GROUP BY flk2.user_id
                    ORDER BY COUNT(*) DESC
                    LIMIT 1)
                AND flk.film_id NOT IN (
                    SELECT flk4.film_id
                    FROM films_likes flk4
                    WHERE flk4.user_id = ?)
                """;
        return jdbcTemplate.query(sql, filmRowMapper, userId, userId, userId);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        StringBuilder sql = new StringBuilder("SELECT films.* "
                + "FROM films "
                + "LEFT JOIN films_likes ON films.film_id = films_likes.film_id "
                + "LEFT JOIN MPA ON MPA.mpa_id = films.film_mpa "
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
        String sqlQuery = sql.toString();
        return jdbcTemplate.query(sqlQuery, filmRowMapper);
    }
}