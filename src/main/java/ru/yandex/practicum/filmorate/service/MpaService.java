package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> getAll() {
        return mpaStorage.getAllMpas();
    }

    public Mpa getById(int id) {
        if (!mpaStorage.isExist(id)) {
            throw new NotFoundException(String.format("Mpa with ID %d does not exist.", id));
        }
        return mpaStorage.getMpaById(id);
    }
}
