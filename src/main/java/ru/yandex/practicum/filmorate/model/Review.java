package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private int reviewId;
    @NotBlank(message = "Content should not be empty")
    private String content;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
    @NotNull
    private Boolean isPositive;

    private int useful;
}
