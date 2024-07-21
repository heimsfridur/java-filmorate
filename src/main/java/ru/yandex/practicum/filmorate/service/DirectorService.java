package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        directorStorage.checkDirector(director.getId());
        return directorStorage.updateDirector(director);
    }

    public Director getDirectorById(Integer id) {
        return directorStorage.getDirectorById(id);
    }

    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public String deleteDirector(Integer directorId) {
        if (directorStorage.deleteDirector(directorId)) {
            return "Director is successfully deleted";
        } else {
            return "Director is deleted recently";
        }
    }
}
