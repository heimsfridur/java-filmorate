package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;

    @Email
    @NotEmpty
    private String email;

    @NotBlank(message = "Login should not be empty.")
    @Pattern(regexp = "\\S+", message = "Login must not contain spaces.")
    private String login;

    private String name;

    @PastOrPresent(message = "Birthday should not be in the future.")
    @NotNull
    private LocalDate birthday;
}
