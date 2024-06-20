package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> getAllMpas() {
        try {
            return mpaService.getAllMpas();
        } catch (Exception e) {
            log.error("Error getting all MPAs", e);
            return null;
        }
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Integer id) {
        return mpaService.getMpaById(id);
    }
}
