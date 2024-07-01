package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    public List<Mpa> getAllMpas();

    public Mpa getMpaById(int id);

    public boolean isExist(int mpaId);
}
