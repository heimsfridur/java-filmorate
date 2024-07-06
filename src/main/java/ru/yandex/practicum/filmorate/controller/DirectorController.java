package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping(value = "/directors", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("Adding new director: " + director);
        return directorService.addDirector(director);
    }

    @PutMapping(value = "/directors", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Updating director: " + director.getId());
        return directorService.updateDirector(director);
    }

    @GetMapping(value = "/directors/{id}", produces = APPLICATION_JSON_VALUE)
    public Director getDirector(@PathVariable(name = "id") Integer id) {
        log.info("Getting director with id " + id + ": ");
        return directorService.getDirectorById(id);
    }

    @GetMapping(value = "/directors", produces = APPLICATION_JSON_VALUE)
    public List<Director> getDirectors() {
        log.info("Whole list of directors:");
        return directorService.getDirectors();
    }

    @DeleteMapping(value = "/directors/{id}", produces = APPLICATION_JSON_VALUE)
    public String deleteDirector(@PathVariable(name = "id") Integer id) {
        log.info("Deleting director with id " + id + ":");
        return directorService.deleteDirector(id);
    }
}
