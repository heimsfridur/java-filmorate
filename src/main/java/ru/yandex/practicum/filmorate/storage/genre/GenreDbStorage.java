package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Slf4j
@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public Genre getById(int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, genreRowMapper, id);
    }

    @Override
    public boolean isExist(int genreId) {
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return count != null && count > 0;
    }

    @Override
    public boolean allGenresExist(Set<Genre> genres) {
        List<Integer> genreIds = genres.stream().map(Genre::getId).collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(genreIds.size(), "?"));

        String sql = "SELECT COUNT(*) FROM genres " +
                "WHERE genre_id IN (" + inSql + ")";

        int count = jdbcTemplate.queryForObject(sql, Integer.class, genreIds.toArray(new Integer[0]));
        return count == genreIds.size();
    }

    @Override
    public void setGenresForFilm(Film film, Set<Genre> genres) {
        String insertGenreSql = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(insertGenreSql,
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Genre genre = (Genre) genres.toArray()[i];
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genre.getId());
                    }

                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    @Override
    public void loadGenresForFilms(List<Film> films) {
        final Map<Integer, Film> filmById = films.stream()
                .collect(Collectors.toMap(Film::getId, identity()));
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery = "SELECT fg.*, g.* " +
                "FROM genres g " +
                "JOIN films_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id IN (" + inSql + ")";

        jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> {
                    final int filmId = rs.getInt("film_id");
                    log.debug("filmId {} ", filmId);
                    final Film film = filmById.get(filmId);
                    log.debug("film {} ", film);
                    Genre genre = genreRowMapper.mapRow(rs, rowNum);
                    log.debug("genre {} ", genre);
                    if (film.getGenres() == null) {
                        film.setGenres(new LinkedHashSet<>());
                    }
                    film.getGenres().add(genre);
                    return film;
                },
                films.stream().map(Film::getId).toArray());
    }

    @Override
    public List<Genre> getGenresListForFilm(Integer filmId) {
        String sql = "SELECT fg.*, g.genre_name\n" +
                "FROM films_genres AS fg JOIN genres AS g ON g.genre_id = fg.genre_id\n" +
                "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, genreRowMapper, filmId);
    }


}
