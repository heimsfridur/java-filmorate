package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@Import({FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class})
public class FilmDbStorageTest {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final GenreStorage genreStorage;

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldGetFilmByIdTest() {

        Film film = filmStorage.getById(1);

        assertThat(film)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldGtAllFilmsTest() {
        List<Film> films = filmStorage.getAll();

        assertEquals(2, films.size(), "The size of films list is incorrect.");
        assertThat(films.get(0).getName()).isEqualTo("Film One");
        assertThat(films.get(1).getName()).isEqualTo("Film Two");
    }

    @Test
    @DirtiesContext
    public void shouldAddFilmTest() {
        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(genre);

        Mpa mpa = Mpa.builder().id(1).name("G").build();
        Film film = Film.builder()
                .name("film1")
                .description("descr1")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .mpa(mpa)
                .genres(genres)
                .directors(directorStorage.getDirectorListFromFilm(1))
                .build();

        Film savedFilm = filmStorage.add(film);

        List<Film> films = filmStorage.getAll();
        assertEquals(1, films.size(), "The film was not added.");
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", 1);

        assertThat(savedFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "film1");
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldUpdateFilmsTest() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(genre);

        Film newFilm = Film.builder()
                .id(1)
                .name("new_film1")
                .description("new_descr1")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .mpa(mpa)
                .genres(genres)
                .directors(directorStorage.getDirectorListFromFilm(1))
                .build();
        filmStorage.update(newFilm);

        Film updatedFilm = filmStorage.getById(1);
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("id", 1);
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "new_film1");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", "new_descr1");
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldGetPopularFilmsTest() {
        Set<Genre> genreSet = Collections.singleton(Genre.builder().id(1).name("Комедия").build());
        filmStorage.addLikeToFilm(1, 1);
        filmStorage.addLikeToFilm(1, 2);
        filmStorage.addLikeToFilm(2, 1);
        genreStorage.setGenresForFilm(filmStorage.getById(1), genreSet);
        genreStorage.setGenresForFilm(filmStorage.getById(2), genreSet);
        List<Film> popularFilms = filmStorage.getPopular(5, 1, 2024);
        assertEquals(2, popularFilms.size(), "The amount of popular films is wrong.");
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("id", 1);
        assertThat(popularFilms.get(1)).hasFieldOrPropertyWithValue("id", 2);
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldAddLikeToFilmTest() {
        filmStorage.addLikeToFilm(1, 2);

        Film film = filmStorage.getById(1);
        int likesAmount = filmStorage.getAmountOfLikes(film);

        assertEquals(1, likesAmount, "The amount of likes is incorrect.");
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldDeleteLikeFromFilmTest() {
        filmStorage.addLikeToFilm(1, 2);
        filmStorage.deleteLikeFromFilm(1, 2);


        Film film = filmStorage.getById(1);
        int likesAmount = filmStorage.getAmountOfLikes(film);

        assertEquals(0, likesAmount, "Like was not deleted.");
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void testGetCommonFilms() {
        filmStorage.addLikeToFilm(1, 1);
        filmStorage.addLikeToFilm(1, 2);
        filmStorage.addLikeToFilm(2, 1);
        filmStorage.addLikeToFilm(2, 2);
        List<Film> commonFilms = filmStorage.getCommonFilms(1, 2);

        assertEquals(2, commonFilms.size(), "The amount of common films is incorrect.");
        assertThat(commonFilms.get(0)).hasFieldOrPropertyWithValue("id", 1);
        assertThat(commonFilms.get(1)).hasFieldOrPropertyWithValue("id", 2);
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void testGetRecommendationsForUser() {
        filmStorage.addLikeToFilm(1, 1);
        filmStorage.addLikeToFilm(1, 2);
        List<Film> recommendations = filmStorage.getRecommendations(3);

        assertNotNull(recommendations);
    }
}
