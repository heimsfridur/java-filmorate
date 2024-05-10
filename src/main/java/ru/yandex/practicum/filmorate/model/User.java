package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;

    @Email
    private String email;

    @NotBlank(message = "Login should not be empty.")
    @Pattern(regexp = "\\S+", message = "Login must not contain spaces.")
    private String login;

    private String name;

    @PastOrPresent(message = "Birthday should not be in the future.")
    private LocalDate birthday;
}
