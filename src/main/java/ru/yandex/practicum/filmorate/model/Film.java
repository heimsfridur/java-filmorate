package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.ValidReleaseDate;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

/**
 * Film.
 */

@Data
@Builder
@AllArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "The  film name should not be empty")
    private String name;

    @Size(max = 200, message = "The length of the description should not exceed 200 characters")
    private String description;

    @NotNull
    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Set<Integer> likesFromUsers = new HashSet<>();

    private Mpa mpa;

    private Set<Genre> genres = new HashSet<>();
}
