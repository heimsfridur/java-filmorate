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

    public Collection<Mpa> getAllMpas() {
        return mpaStorage.getAllMpas();
    }

    public Mpa getMpaById(int id) {
        try {
            return mpaStorage.getMpaById(id);
        } catch (RuntimeException exc) {
            throw new NotFoundException(String.format("Can not find mpa with id %d", id));
        }
    }
}
