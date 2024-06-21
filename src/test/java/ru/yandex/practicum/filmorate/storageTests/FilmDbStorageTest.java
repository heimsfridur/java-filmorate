package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(FilmDbStorage.class)
public class FilmDbStorageTest {
    private final FilmStorage filmStorage;

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldGetFilmByIdTest() {

        Film film = filmStorage.getFilmById(1);

        assertThat(film)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldGtAllFilmsTest() {
        List<Film> films = filmStorage.getAllFilms();

        assertEquals(2, films.size(), "The size of films list is incorrect.");
        assertThat(films.get(0).getName()).isEqualTo("Film One");
        assertThat(films.get(1).getName()).isEqualTo("Film Two");
    }

    @Test
    @DirtiesContext
    public void shouldAddFilmTest() {
        Film film = Film.builder()
                .name("film1")
                .description("descr1")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .build();

        Film savedFilm = filmStorage.addFilm(film);

        List<Film> films = filmStorage.getAllFilms();
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

        Film newFilm = Film.builder()
                .id(1)
                .name("new_film1")
                .description("new_descr1")
                .releaseDate(LocalDate.of(2024, 10, 11))
                .duration(150)
                .mpa(mpa)
                .build();
        filmStorage.updateFilm(newFilm);

        Film updatedFilm = filmStorage.getFilmById(1);
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("id", 1);
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "new_film1");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", "new_descr1");
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldGetPopularFilmsTest() {
        filmStorage.addLikeToFilm(2, 1);
        List<Film> popularFilms = filmStorage.getTopFilms(5);

        assertEquals(2, popularFilms.size(), "The amount of popular films is wrong.");
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("id", 2);
        assertThat(popularFilms.get(1)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldAddLikeToFilmTest() {
        filmStorage.addLikeToFilm(1, 2);

        Film film = filmStorage.getFilmById(1);
        Set<Integer> likes = film.getLikesFromUsers();

        assertEquals(1, likes.size(), "The amount of likes is incorrect.");
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldDeleteLikeFromFilmTest() {
        filmStorage.addLikeToFilm(1, 2);
        filmStorage.deleteLikeFromFilm(1, 2);


        Film film = filmStorage.getFilmById(1);
        Set<Integer> likes = film.getLikesFromUsers();

        assertEquals(0, likes.size(), "Like was not deleted.");
    }
}
