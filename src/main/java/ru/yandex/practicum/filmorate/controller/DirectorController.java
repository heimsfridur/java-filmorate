package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("Adding new director: " + director);
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Updating director: " + director.getId());
        return directorService.updateDirector(director);
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable(name = "id") Integer id) {
        log.info("Getting director with id " + id + ": ");
        return directorService.getDirectorById(id);
    }

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Whole list of directors:");
        return directorService.getDirectors();
    }

    @DeleteMapping("/{id}")
    public String deleteDirector(@PathVariable(name = "id") Integer id) {
        log.info("Deleting director with id " + id + ":");
        return directorService.deleteDirector(id);
    }
}
