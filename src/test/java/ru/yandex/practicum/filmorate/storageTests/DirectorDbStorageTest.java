package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorDbStorageTest {

    private final DirectorService directorService;

    @Test
    void addDirectorTest() {
        Director addedDirector = directorService.addDirector(getDirector());
        assertThat(addedDirector).isNotNull();
        assertThat(addedDirector.getId()).isEqualTo(1);
        assertThat(addedDirector.getName()).isEqualTo("Director");
    }

    @Test
    void updateDirectorTest() {
        Director addedDirector = directorService.addDirector(getDirector());
        addedDirector.setName("Adam");
        addedDirector = directorService.updateDirector(addedDirector);
        assertThat(addedDirector).isNotNull();
        assertThat(addedDirector.getId()).isEqualTo(1);
        assertThat(addedDirector.getName()).isEqualTo("Adam");
    }

    @Test
    void getDirectorsTest() {
        directorService.addDirector(getDirector());
        directorService.addDirector(getDirector());
        List<Director> directors = directorService.getDirectors();

        assertThat(directors).isNotNull().hasSize(2);
    }

    @Test
    void getDirectorByIdTest() {
        directorService.addDirector(getDirector());
        Director result = directorService.getDirectorById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void deleteDirectorTest() {
        Director addedDirector = directorService.addDirector(getDirector());
        String result = directorService.deleteDirector(addedDirector.getId());

        assertThat(result).isEqualTo("Director is successfully deleted");
    }

    private Director getDirector() {
        Director director = new Director();
        director.setName("Director");
        return director;
    }
}
