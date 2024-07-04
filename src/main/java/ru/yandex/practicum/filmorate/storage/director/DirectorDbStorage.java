package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.AddException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorRowMapper;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Slf4j
@Component
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private JdbcTemplate jdbcTemplate;

    private DirectorRowMapper mapper;

    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingColumns("director_name")
                .usingGeneratedKeyColumns("director_id");
        try {
            int directorId = simpleJdbcInsert.executeAndReturnKey(mapper.directorToMap(director)).intValue();
            return getDirectorById(directorId);
        } catch (Exception e) {
            throw new AddException(e.getMessage());
        }
    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update("UPDATE directors " +
                        "SET director_name = ? " +
                        "WHERE director_id = ?", director.getName(),
                director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public Director getDirectorById(Integer id) {
        String sql = String.format("SELECT * FROM directors WHERE director_id = %d", id);
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                throw new NotFoundException("Director is not found");
            }
            Director director = mapper.mapRow(rs, rs.getRow());
            if (director != null) {
                return director;
            } else {
                throw new NotFoundException("Director is not found");
            }
        });
    }

    @Override
    public List<Director> getDirectors() {
        List<Director> directorList = new ArrayList<>();
        jdbcTemplate.query("SELECT * FROM directors", rs -> {

            do {
                Director director = mapper.mapRow(rs, rs.getRow());
                if (director != null) {
                    directorList.add(director);
                }
            } while (rs.next());
        });
        return directorList;
    }

    @Override
    public void checkDirector(Integer directorId) {
        jdbcTemplate.query("SELECT * FROM directors WHERE director_id = " + directorId, rs -> {
            if (rs.isBeforeFirst()) {
                throw new NotFoundException("Director with id" + directorId + " is not found.");
            }
        });
    }

    @Override
    public boolean deleteDirector(Integer directorId) {
        try {
            jdbcTemplate.execute(String.format("DELETE directors WHERE director_id = %d ",
                    directorId));
            return true;
        } catch (Exception e) {
            log.error("Error of deleting like");
            return false;
        }
    }

    @Override
    public void setDirectorsForFilm(List<Director> directors, int filmId) {
        directors.forEach(director ->
                jdbcTemplate.execute(String.format("MERGE INTO films_directors (film_id, director_id) " +
                "VALUES (%d, %d)", filmId, director.getId())));

    }

    @Override
    public void loadDirectorsForFilms(List<Film> films) {
        final Map<Integer, Film> filmById = films.stream()
                .collect(Collectors.toMap(Film::getId, identity()));
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery =  "SELECT fd.*, d.* " +
                "FROM directors d " +
                "JOIN films_directors fd ON d.director_id = fd.director_id " +
                "WHERE fd.film_id IN (" + inSql + ")";

        jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> {
                    final int filmId = rs.getInt("film_id");
                    log.debug("filmId {} ", filmId);
                    final Film film = filmById.get(filmId);
                    log.debug("film {} ", film);
                    Director director = mapper.mapRow(rs, rowNum);
                    log.debug("director {} ", director);
                    if (film.getDirectors() == null) {
                        film.setDirectors(new ArrayList<>());
                    }
                    film.getDirectors().add(director);
                    return film;
                },
                films.stream().map(Film::getId).toArray());
    }


}
