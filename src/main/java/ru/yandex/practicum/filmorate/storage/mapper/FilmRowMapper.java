package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        Integer id = rs.getInt("film_id");
        String name = rs.getString("film_name");
        String description = rs.getString("film_description");
        LocalDate releaseDate = rs.getDate("film_releaseDate").toLocalDate();
        Integer duration = rs.getInt("film_duration");
        Mpa mpa = mpaStorage.getMpaById(rs.getInt("film_mpa"));
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(genreStorage.getGenresListForFilm(id));
        List<Director> directors = directorStorage.getDirectorListFromFilm(id);
        return rs.wasNull() ? null : new Film(id, name, description, releaseDate, duration, mpa, genres, directors);
    }
}
