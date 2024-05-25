package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    HashMap<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        user.setId(userId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info(String.format("User with id %d was added.", userId - 1));
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User newUser) {
        int userId = newUser.getId();

        if (users.containsKey(userId)) {
            if (newUser.getName() == null) {
                newUser.setName(newUser.getLogin());
            }
            users.put(userId, newUser);
            log.info(String.format("User with id %d was updated", userId));
            return newUser;
        }

        log.warn(String.format("Can not update user. There is no user with id %d", userId));
        throw new NotFoundException(String.format("User with id %d is not found", newUser.getId()));
    }
}
